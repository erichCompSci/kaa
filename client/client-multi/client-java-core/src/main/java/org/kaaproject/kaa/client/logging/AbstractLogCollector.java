/*
 * Copyright 2014 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.client.logging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.kaaproject.kaa.client.channel.FailoverManager;
import org.kaaproject.kaa.client.channel.KaaChannelManager;
import org.kaaproject.kaa.client.channel.LogTransport;
import org.kaaproject.kaa.client.channel.TransportConnectionInfo;
import org.kaaproject.kaa.client.context.ExecutorContext;
import org.kaaproject.kaa.client.logging.memory.MemLogStorage;
import org.kaaproject.kaa.common.TransportType;
import org.kaaproject.kaa.common.endpoint.gen.LogDeliveryErrorCode;
import org.kaaproject.kaa.common.endpoint.gen.LogDeliveryStatus;
import org.kaaproject.kaa.common.endpoint.gen.LogEntry;
import org.kaaproject.kaa.common.endpoint.gen.LogSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.LogSyncResponse;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponseResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference implementation of @see LogCollector
 * 
 * @author Andrew Shvayka
 */
public abstract class AbstractLogCollector implements LogCollector, LogProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLogCollector.class);

    public static final long MAX_BATCH_VOLUME = 512 * 1024; // Framework
                                                            // limitation

    protected final ExecutorContext executorContext;
    private final LogTransport transport;
    private final Set<Integer> timeouts = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    private final KaaChannelManager channelManager;
    private final FailoverManager failoverManager;

    protected LogStorage storage;
    private LogUploadStrategy strategy;
    private LogFailoverCommand controller;

    private final Object uploadCheckLock = new Object();
    private boolean uploadCheckInProgress = false;

    public AbstractLogCollector(LogTransport transport, ExecutorContext executorContext,
                                KaaChannelManager channelManager, FailoverManager failoverManager) {
        this.strategy = new DefaultLogUploadStrategy();
        this.storage = new MemLogStorage(strategy.getBatchSize(), strategy.getBatchCount());
        this.controller = new DefaultLogUploadController();
        this.channelManager = channelManager;
        this.transport = transport;
        this.executorContext = executorContext;
        this.failoverManager = failoverManager;
    }

    @Override
    public void setStrategy(LogUploadStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy is null!");
        }
        this.strategy = strategy;
        LOG.info("New log upload strategy was set: {}", strategy);
    }

    @Override
    public void setStorage(LogStorage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Storage is null!");
        }
        this.storage = storage;
        LOG.info("New log storage was set {}", storage);
    }

    @Override
    public void fillSyncRequest(LogSyncRequest request) {
        LogBlock group = null;
        if (storage.getStatus().getRecordCount() == 0) {
            LOG.debug("Log storage is empty");
            return;
        }
        group = storage.getRecordBlock(strategy.getBatchSize(), strategy.getBatchCount());

        if (group != null) {
            List<LogRecord> recordList = group.getRecords();

            if (!recordList.isEmpty()) {
                LOG.trace("Sending {} log records", recordList.size());

                List<LogEntry> logs = new LinkedList<>();
                for (LogRecord record : recordList) {
                    logs.add(new LogEntry(ByteBuffer.wrap(record.getData())));
                }

                request.setRequestId(group.getBlockId());
                request.setLogEntries(logs);

                LOG.info("Adding following bucket id [{}] for timeout tracking", group.getBlockId());
                timeouts.add(group.getBlockId());

                final LogBlock timeoutGroup = group;
                executorContext.getScheduledExecutor().schedule(new Runnable() {
                    @Override
                    public void run() {
                        checkDeliveryTimeout(timeoutGroup.getBlockId());
                    }
                }, strategy.getTimeout(), TimeUnit.SECONDS);
            }
        } else {
            LOG.warn("Log group is null: log group size is too small");
        }
    }

    @Override
    public synchronized void onLogResponse(LogSyncResponse logSyncResponse) throws IOException {
        if (logSyncResponse.getDeliveryStatuses() != null) {
            boolean isAlreadyScheduled = false;
            for (LogDeliveryStatus response : logSyncResponse.getDeliveryStatuses()) {
                if (response.getResult() == SyncResponseResultType.SUCCESS) {
                    storage.removeRecordBlock(response.getRequestId());
                } else {
                    storage.notifyUploadFailed(response.getRequestId());
                    final LogDeliveryErrorCode errorCode = response.getErrorCode();
                    final LogFailoverCommand controller = this.controller;
                    executorContext.getCallbackExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            strategy.onFailure(controller, errorCode);
                        }
                    });
                    isAlreadyScheduled = true;
                }
                LOG.info("Removing bucket id from timeouts: {}", response.getRequestId());
                timeouts.remove(response.getRequestId());
            }

            if (!isAlreadyScheduled) {
                processUploadDecision(strategy.isUploadNeeded(storage.getStatus()));
            }
        }
    }

    @Override
    public void stop() {
        storage.close();
    }

    private void processUploadDecision(LogUploadStrategyDecision decision) {
        switch (decision) {
        case UPLOAD:
            transport.sync();
            break;
        case NOOP:
            if (strategy.getUploadCheckPeriod() > 0 && storage.getStatus().getRecordCount() > 0) {
                scheduleUploadCheck();
            }
            break;
        default:
            break;
        }
    }

    protected void scheduleUploadCheck() {
        LOG.trace("Attempt to execute upload check: {}", uploadCheckInProgress);
        synchronized (uploadCheckLock) {
            if (!uploadCheckInProgress) {
                LOG.trace("Scheduling upload check with timeout: {}", strategy.getUploadCheckPeriod());
                uploadCheckInProgress = true;
                executorContext.getScheduledExecutor().schedule(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (uploadCheckLock) {
                            uploadCheckInProgress = false;
                        }
                        uploadIfNeeded();
                    }
                }, strategy.getUploadCheckPeriod(), TimeUnit.SECONDS);
            } else {
                LOG.trace("Upload check is already scheduled!");
            }
        }
    }

    private boolean checkDeliveryTimeout(int bucketId) {
        LOG.debug("Checking for a delivery timeout of the bucket with id: [{}] ", bucketId);
        boolean isTimeout = timeouts.remove(bucketId);

        if (isTimeout) {
            LOG.info("Log delivery timeout detected for the bucket with id: [{}]", bucketId);
            storage.notifyUploadFailed(bucketId);
            final LogFailoverCommand controller = this.controller;
            executorContext.getCallbackExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    strategy.onTimeout(controller);
                }
            });
        } else {
            LOG.trace("No log delivery timeout for the bucket with id [{}] was detected", bucketId);
        }

        return isTimeout;
    }

    protected void uploadIfNeeded() {
        processUploadDecision(strategy.isUploadNeeded(storage.getStatus()));
    }

    private class DefaultLogUploadController implements LogFailoverCommand {
        @Override
        public void switchAccessPoint() {
            TransportConnectionInfo server = channelManager.getActiveServer(TransportType.LOGGING);
            if (server != null) {
                failoverManager.onServerFailed(server);
            } else {
                LOG.warn("Failed to switch Operation server. No channel is used for logging transport");
            }
        }

        @Override
        public void retryLogUpload() {
            uploadIfNeeded();
        }

        @Override
        public void retryLogUpload(int delay) {
            executorContext.getScheduledExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    uploadIfNeeded();
                }
            }, delay, TimeUnit.SECONDS);
        }
    }
}

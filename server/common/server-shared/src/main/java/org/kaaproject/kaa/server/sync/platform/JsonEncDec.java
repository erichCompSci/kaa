/*
 * Copyright 2015 CyberVision, Inc.
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
package org.kaaproject.kaa.server.sync.platform;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.kaaproject.kaa.common.Constants;
import org.kaaproject.kaa.common.endpoint.security.KeyUtil;
import org.kaaproject.kaa.server.sync.*;
import org.kaaproject.kaa.server.sync.ClientSyncLight.JsonEvent;
import org.kaaproject.kaa.server.sync.ClientSyncLight.JsonProtocolVersionId;
import org.kaaproject.kaa.server.sync.ClientSyncLight.JsonTopicState;
import org.kaaproject.kaa.server.sync.bootstrap.BootstrapClientSync;
import org.kaaproject.kaa.server.sync.bootstrap.ProtocolVersionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * This class is an implementation of {@link PlatformEncDec} that uses JSON for
 * data serialization.
 *
 *
 */
@KaaPlatformProtocol
public class JsonEncDec implements PlatformEncDec {
    private static final Logger LOG = LoggerFactory.getLogger(JsonEncDec.class);

    private ObjectMapper jsonMapper;
    
    public JsonEncDec(){
        super();
        jsonMapper = new ObjectMapper();
        jsonMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    @Override
    public int getId() {
        return Constants.KAA_PLATFORM_PROTOCOL_JSON_ID;
    }

    @Override
    public ClientSync decode(byte[] data) throws PlatformEncDecException {
        ClientSyncLight source = null;
        try {
            source = jsonMapper.readValue(data, ClientSyncLight.class);
        } catch (Exception e) {
            throw new PlatformEncDecException(e);
        }
        if (source == null)
            return null;
        LOG.trace("Decoding client sync {}", source);
        ClientSync sync = unwrapClient(source);
        LOG.trace("Decoded client sync {}", sync);
        return sync;
    }

    @Override
    public byte[] encode(ServerSync sync) throws PlatformEncDecException {
        try {
            return jsonMapper.writeValueAsString(sync).getBytes(Charset.forName("UTF-8"));
        } catch (Exception e) {
            throw new PlatformEncDecException(e);
        }
    }

    private ClientSync unwrapClient(ClientSyncLight source) throws PlatformEncDecException {
        ClientSync sync = new ClientSync();
        sync.setRequestId(source.getRequestId());
        ClientSyncMetaData metadata = new ClientSyncMetaData();
        if (source.getEndpointPublicKeyHash() != null && source.getEndpointPublicKeyHash().length != 0) {
            metadata.setEndpointPublicKeyHash(ByteBuffer.wrap(source.getEndpointPublicKeyHash()));
        } else {
            metadata.setEndpointPublicKeyHash(generateEndointKey());
        }
        if (source.getSdkToken() == null || source.getSdkToken().isEmpty())
            throw new PlatformEncDecException("SDK token not initialized");
        metadata.setSdkToken(source.getSdkToken());
        if (source.getProfileHash() != null)
            metadata.setProfileHash(ByteBuffer.wrap(source.getProfileHash()));
        sync.setClientSyncMetaData(metadata);
        if (source.getBootstrapKeys() != null) {
            List<ProtocolVersionId> keys = new ArrayList<ProtocolVersionId>();
            for (JsonProtocolVersionId key : source.getBootstrapKeys()) {
                keys.add(new ProtocolVersionId(key.getProtocolId(), key.getVersion()));
            }
            BootstrapClientSync bootstrapSync = new BootstrapClientSync(source.getBootstrapRequestId(), keys);
            sync.setBootstrapSync(bootstrapSync);
        }
        if (source.getProfileBody() != null && source.getProfileEndpointPublicKey() != null
                && source.getProfileEndpointAccessToken() != null) {
            ProfileClientSync profileSync = new ProfileClientSync(
                    ByteBuffer.wrap(source.getProfileEndpointPublicKey()), ByteBuffer.wrap(source.getProfileBody()),
                    null, source.getProfileEndpointAccessToken());
            sync.setProfileSync(profileSync);
        }
        if (source.getConfigurationHash() != null) {
            ConfigurationClientSync configurationSync = new ConfigurationClientSync(source.getConfAppStateSeqNumber(),
                    ByteBuffer.wrap(source.getConfigurationHash()), source.isConfResyncOnly());
            sync.setConfigurationSync(configurationSync);
        }
        if (source.getNotificationTopicListHash() != null) {
            List<TopicState> topicStates = new ArrayList<TopicState>();
            for (JsonTopicState topicState : source.getNotificationTopicStates()) {
                topicStates.add(new TopicState(topicState.getTopicId(), topicState.getSeqNumber()));
            }
            NotificationClientSync notificationSync = new NotificationClientSync(
                    source.getNotificationAppStateSeqNumber(), ByteBuffer.wrap(source.getNotificationTopicListHash()),
                    topicStates, source.getAcceptedUnicastNotifications(), source.getNotificationSubscriptionCommands());
            sync.setNotificationSync(notificationSync);
        }
        if (source.getUserAttachRequest() != null || source.getUserEndpointAttachRequests() != null
                || source.getUserEndpointDetachRequests() != null) {
            UserAttachRequest request = null;
            if (source.getUserAttachRequest() != null) {
                request = new UserAttachRequest(source.getUserAttachRequest().getUserVerifierId(), source
                        .getUserAttachRequest().getUserExternalId(), source.getUserAttachRequest().getUserAccessToken());
            }
            UserClientSync userSync = new UserClientSync(request, source.getUserEndpointAttachRequests(),
                    source.getUserEndpointDetachRequests());
            sync.setUserSync(userSync);
        }
        if (source.getEvents() != null && source.getEventListenersRequests() != null) {
            List<Event> events = new ArrayList<Event>();
            for (JsonEvent event : source.getEvents()) {
                events.add(new Event(event.getSeqNum(), event.getEventClassFQN(),
                        ByteBuffer.wrap(event.getEventData()), event.getSource(), event.getTarget()));
            }
            EventClientSync eventSync = new EventClientSync(source.isEventSeqNumberRequest(),
                    source.getEventListenersRequests(), events);
            sync.setEventSync(eventSync);
        }
        if (source.getLogsSync() != null) {
            List<LogEntry> logEntries = new ArrayList<LogEntry>();
            for (byte[] entry : source.getLogsSync()) {
                logEntries.add(new LogEntry(ByteBuffer.wrap(entry)));
            }
            LogClientSync logSync = new LogClientSync(source.getLogReqId(), logEntries);
            sync.setLogSync(logSync);
        }

        return sync;
    }

    private ByteBuffer generateEndointKey() throws PlatformEncDecException {
        try {
            return ByteBuffer.wrap(KeyUtil.generateKeyPair().getPublic().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new PlatformEncDecException(e);
        }
    }

}

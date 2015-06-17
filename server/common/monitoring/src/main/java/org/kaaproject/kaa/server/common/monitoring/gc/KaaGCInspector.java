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

package org.kaaproject.kaa.server.common.monitoring.gc;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import org.kaaproject.kaa.server.common.monitoring.MonitoringOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KaaGCInspector implements NotificationListener {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(KaaGCInspector.class);

    private final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    private final ObjectName gcName;

    private Map<String, GCAlarmCallback> gcListeners = new ConcurrentHashMap<>();

    @Autowired
    private MonitoringOptions monitoringOptions;

    public KaaGCInspector() {
        try {
            gcName = new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*");
        } catch (Exception e) {
            LOG.error("Can't initialize kaa gc inspector.");
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void start() {
        try {
            for (ObjectName name : server.queryNames(gcName, null)) {
                server.addNotificationListener(name, this, null, null);
            }
        } catch (Exception e) {
            LOG.error("Can't initialize kaa gc inspector.");
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void stop() {
        try {
            for (ObjectName name : server.queryNames(gcName, null)) {
                server.removeNotificationListener(name, this);
            }
        } catch (Exception e) {
            LOG.warn("Can't correct destroy kaa gc inspector.");
        }
    }

    @Override
    public void handleNotification(Notification notification, Object o) {
        String type = notification.getType();
        if (type.equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            CompositeData cd = (CompositeData) notification.getUserData();
            GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from(cd);
            String gcName = info.getGcName();
            GcInfo gcInfo = info.getGcInfo();
            if (isOldGenGC(gcName)) {
                long duration = gcInfo.getDuration();
                if(duration>monitoringOptions.getGcWarnDurationTime()) {
                    LOG.warn("Handle FullGC with duration more than {}", monitoringOptions.getGcWarnDurationTime());
                    for (Map.Entry<String, GCAlarmCallback> entry : gcListeners.entrySet()) {
                        entry.getValue().onGCAlarm(duration);
                    }
                }
            }
        }
    }

    public void addGCListener(String name, GCAlarmCallback callback) {
        gcListeners.put(name, callback);
    }

    private boolean isOldGenGC(String gcName) {
        boolean isOldGen = false;
        if ("MarkSweepCompact".equals(gcName) || "PS MarkSweep".equals(gcName) || "ConcurrentMarkSweep".equals(gcName) || "G1 Old Generation".equals(gcName)) {
            isOldGen = true;
        }
        return isOldGen;
    }
}

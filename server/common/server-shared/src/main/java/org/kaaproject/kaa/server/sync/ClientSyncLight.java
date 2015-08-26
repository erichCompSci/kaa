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
package org.kaaproject.kaa.server.sync;

import java.util.List;

public class ClientSyncLight {

    private int requestId;

    // The client sync meta data.
    private String sdkToken;
    private byte[] endpointPublicKeyHash;
    private byte[] profileHash;

    // The client sync meta data.
    private int bootstrapRequestId;
    private List<JsonProtocolVersionId> bootstrapKeys;

    // The profile sync.
    private byte[] profileEndpointPublicKey;
    private byte[] profileBody;
    private String profileEndpointAccessToken;

    // The configuration sync.
    private int confAppStateSeqNumber;
    private byte[] configurationHash;
    private boolean confResyncOnly;

    // The notification sync.
    private int notificationAppStateSeqNumber;
    private byte[] notificationTopicListHash;
    private List<JsonTopicState> notificationTopicStates;
    private List<String> acceptedUnicastNotifications;
    private List<SubscriptionCommand> notificationSubscriptionCommands;

    // The user sync.
    private JsonUserAttachRequest userAttachRequest;
    private List<EndpointAttachRequest> userEndpointAttachRequests;
    private List<EndpointDetachRequest> userEndpointDetachRequests;

    // The event sync.
    private boolean eventSeqNumberRequest;
    private List<EventListenersRequest> eventListenersRequests;
    private List<JsonEvent> events;

    // The log sync.
    private int logReqId;
    private List<byte[]> logsSync;

    public List<EndpointAttachRequest> getUserEndpointAttachRequests() {
        return userEndpointAttachRequests;
    }

    public void setUserEndpointAttachRequests(List<EndpointAttachRequest> userEndpointAttachRequests) {
        this.userEndpointAttachRequests = userEndpointAttachRequests;
    }

    public List<EndpointDetachRequest> getUserEndpointDetachRequests() {
        return userEndpointDetachRequests;
    }

    public void setUserEndpointDetachRequests(List<EndpointDetachRequest> userEndpointDetachRequests) {
        this.userEndpointDetachRequests = userEndpointDetachRequests;
    }

    public void setBootstrapRequestId(int bootstrapRequestId) {
        this.bootstrapRequestId = bootstrapRequestId;
    }

    public void setBootstrapKeys(List<JsonProtocolVersionId> bootstrapKeys) {
        this.bootstrapKeys = bootstrapKeys;
    }

    public JsonUserAttachRequest getUserAttachRequest() {
        return userAttachRequest;
    }

    public void setUserAttachRequest(JsonUserAttachRequest userAttachRequest) {
        this.userAttachRequest = userAttachRequest;
    }

    public int getBootstrapRequestId() {
        return bootstrapRequestId;
    }

    public List<JsonProtocolVersionId> getBootstrapKeys() {
        return bootstrapKeys;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getSdkToken() {
        return sdkToken;
    }

    public void setSdkToken(String sdkToken) {
        this.sdkToken = sdkToken;
    }

    public byte[] getEndpointPublicKeyHash() {
        return endpointPublicKeyHash;
    }

    public void setEndpointPublicKeyHash(byte[] endpointPublicKeyHash) {
        this.endpointPublicKeyHash = endpointPublicKeyHash;
    }

    public byte[] getProfileHash() {
        return profileHash;
    }

    public void setProfileHash(byte[] profileHash) {
        this.profileHash = profileHash;
    }

    public byte[] getProfileEndpointPublicKey() {
        return profileEndpointPublicKey;
    }

    public void setProfileEndpointPublicKey(byte[] profileEndpointPublicKey) {
        this.profileEndpointPublicKey = profileEndpointPublicKey;
    }

    public byte[] getProfileBody() {
        return profileBody;
    }

    public void setProfileBody(byte[] profileBody) {
        this.profileBody = profileBody;
    }

    public String getProfileEndpointAccessToken() {
        return profileEndpointAccessToken;
    }

    public void setProfileEndpointAccessToken(String profileEndpointAccessToken) {
        this.profileEndpointAccessToken = profileEndpointAccessToken;
    }

    public int getConfAppStateSeqNumber() {
        return confAppStateSeqNumber;
    }

    public void setConfAppStateSeqNumber(int confAppStateSeqNumber) {
        this.confAppStateSeqNumber = confAppStateSeqNumber;
    }

    public byte[] getConfigurationHash() {
        return configurationHash;
    }

    public void setConfigurationHash(byte[] configurationHash) {
        this.configurationHash = configurationHash;
    }

    public boolean isConfResyncOnly() {
        return confResyncOnly;
    }

    public void setConfResyncOnly(boolean confResyncOnly) {
        this.confResyncOnly = confResyncOnly;
    }

    public int getNotificationAppStateSeqNumber() {
        return notificationAppStateSeqNumber;
    }

    public void setNotificationAppStateSeqNumber(int notificationAppStateSeqNumber) {
        this.notificationAppStateSeqNumber = notificationAppStateSeqNumber;
    }

    public byte[] getNotificationTopicListHash() {
        return notificationTopicListHash;
    }

    public void setNotificationTopicListHash(byte[] notificationTopicListHash) {
        this.notificationTopicListHash = notificationTopicListHash;
    }

    public List<JsonTopicState> getNotificationTopicStates() {
        return notificationTopicStates;
    }

    public void setNotificationTopicStates(List<JsonTopicState> notificationTopicStates) {
        this.notificationTopicStates = notificationTopicStates;
    }

    public List<String> getAcceptedUnicastNotifications() {
        return acceptedUnicastNotifications;
    }

    public void setAcceptedUnicastNotifications(List<String> acceptedUnicastNotifications) {
        this.acceptedUnicastNotifications = acceptedUnicastNotifications;
    }

    public List<SubscriptionCommand> getNotificationSubscriptionCommands() {
        return notificationSubscriptionCommands;
    }

    public void setNotificationSubscriptionCommands(List<SubscriptionCommand> notificationSubscriptionCommands) {
        this.notificationSubscriptionCommands = notificationSubscriptionCommands;
    }

    public boolean isEventSeqNumberRequest() {
        return eventSeqNumberRequest;
    }

    public void setEventSeqNumberRequest(boolean eventSeqNumberRequest) {
        this.eventSeqNumberRequest = eventSeqNumberRequest;
    }

    public List<EventListenersRequest> getEventListenersRequests() {
        return eventListenersRequests;
    }

    public void setEventListenersRequests(List<EventListenersRequest> eventListenersRequests) {
        this.eventListenersRequests = eventListenersRequests;
    }

    public List<JsonEvent> getEvents() {
        return events;
    }

    public void setEvents(List<JsonEvent> events) {
        this.events = events;
    }

    public List<byte[]> getLogsSync() {
        return logsSync;
    }

    public void setLogsSync(List<byte[]> logsSync) {
        this.logsSync = logsSync;
    }

    public int getLogReqId() {
        return logReqId;
    }

    public void setLogReqId(int logReqId) {
        this.logReqId = logReqId;
    }

    public static class JsonEvent {
        private int seqNum;
        private String eventClassFQN;
        private byte[] eventData;
        private String source;
        private String target;

        public JsonEvent() {
        }

        public JsonEvent(int seqNum, String eventClassFQN, byte[] eventData, String source, String target) {
            this.seqNum = seqNum;
            this.eventClassFQN = eventClassFQN;
            this.eventData = eventData;
            this.source = source;
            this.target = target;
        }

        public int getSeqNum() {
            return seqNum;
        }

        public void setSeqNum(int seqNum) {
            this.seqNum = seqNum;
        }

        public String getEventClassFQN() {
            return eventClassFQN;
        }

        public void setEventClassFQN(String eventClassFQN) {
            this.eventClassFQN = eventClassFQN;
        }

        public byte[] getEventData() {
            return eventData;
        }

        public void setEventData(byte[] eventData) {
            this.eventData = eventData;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }

    public static class JsonUserAttachRequest {
        private String userVerifierId;
        private String userExternalId;
        private String userAccessToken;

        public JsonUserAttachRequest() {
        }

        public JsonUserAttachRequest(String userVerifierId, String userExternalId, String userAccessToken) {
            this.userVerifierId = userVerifierId;
            this.userExternalId = userExternalId;
            this.userAccessToken = userAccessToken;
        }

        public String getUserVerifierId() {
            return userVerifierId;
        }

        public void setUserVerifierId(String userVerifierId) {
            this.userVerifierId = userVerifierId;
        }

        public String getUserExternalId() {
            return userExternalId;
        }

        public void setUserExternalId(String userExternalId) {
            this.userExternalId = userExternalId;
        }

        public String getUserAccessToken() {
            return userAccessToken;
        }

        public void setUserAccessToken(String userAccessToken) {
            this.userAccessToken = userAccessToken;
        }
    }

    public static class JsonProtocolVersionId {
        private int protocolId;
        private int version;

        public JsonProtocolVersionId() {
        }

        public JsonProtocolVersionId(int protocolId, int version) {
            this.protocolId = protocolId;
            this.version = version;
        }

        public int getProtocolId() {
            return protocolId;
        }

        public void setProtocolId(int protocolId) {
            this.protocolId = protocolId;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }

    public static class JsonTopicState {
        private String topicId;
        private int seqNumber;

        public JsonTopicState() {
        }

        public JsonTopicState(String topicId, int seqNumber) {
            this.topicId = topicId;
            this.seqNumber = seqNumber;
        }

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public int getSeqNumber() {
            return seqNumber;
        }

        public void setSeqNumber(int seqNumber) {
            this.seqNumber = seqNumber;
        }
    }
}

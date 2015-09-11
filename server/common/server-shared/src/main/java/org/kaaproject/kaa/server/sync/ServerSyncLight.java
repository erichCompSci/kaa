package org.kaaproject.kaa.server.sync;

import java.util.List;
import java.util.Set;

import org.kaaproject.kaa.server.sync.ClientSyncLight.JsonEvent;
import org.kaaproject.kaa.server.sync.bootstrap.ProtocolConnectionData;

public class ServerSyncLight {
    private int requestId;
    private SyncStatus status;
    //bootstrapSync
    private int bootstrapRequestId;
    private Set<ProtocolConnectionData> bootstrapProtocolList;
    //profileSync
    private SyncResponseStatus profileSync;
    //confSync
    private int confAppStateSeqNumber;
    private SyncResponseStatus confResponseStatus;
    private byte[] confSchemaBody;
    private byte[] confDeltaBody;
    //notificationSync
    private int notifAppStateSeqNumber;
    private SyncResponseStatus notifResponseStatus;
    private List<Notification> notifications;
    private List<Topic> notifAvailableTopics;
    //userSync
    private UserAttachResponse userAttachResponse;
    private UserAttachNotification userAttachNotification;
    private UserDetachNotification userDetachNotification;
    private List<EndpointAttachResponse> userEndpointAttachResponses;
    private List<EndpointDetachResponse> userEndpointDetachResponses;
    //eventSync
    private EventSequenceNumberResponse eventSequenceNumberResponse;
    private List<EventListenersResponse> eventListenersResponses;
    private List<JsonEvent> events;
    //redirectSync
    private int redirectAccessPointId;
    //logSync
    private List<LogDeliveryStatus> logDeliveryStatuses;
    
    public int getRequestId() {
        return requestId;
    }
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
    public SyncStatus getStatus() {
        return status;
    }
    public void setStatus(SyncStatus status) {
        this.status = status;
    }
    public int getBootstrapRequestId() {
        return bootstrapRequestId;
    }
    public void setBootstrapRequestId(int bootstrapRequestId) {
        this.bootstrapRequestId = bootstrapRequestId;
    }
    public Set<ProtocolConnectionData> getBootstrapProtocolList() {
        return bootstrapProtocolList;
    }
    public void setBootstrapProtocolList(Set<ProtocolConnectionData> bootstrapProtocolList) {
        this.bootstrapProtocolList = bootstrapProtocolList;
    }
    public SyncResponseStatus getProfileSync() {
        return profileSync;
    }
    public void setProfileSync(SyncResponseStatus profileSync) {
        this.profileSync = profileSync;
    }
    public int getConfAppStateSeqNumber() {
        return confAppStateSeqNumber;
    }
    public void setConfAppStateSeqNumber(int confAppStateSeqNumber) {
        this.confAppStateSeqNumber = confAppStateSeqNumber;
    }
    public SyncResponseStatus getConfResponseStatus() {
        return confResponseStatus;
    }
    public void setConfResponseStatus(SyncResponseStatus confResponseStatus) {
        this.confResponseStatus = confResponseStatus;
    }
    public byte[] getConfSchemaBody() {
        return confSchemaBody;
    }
    public void setConfSchemaBody(byte[] confSchemaBody) {
        this.confSchemaBody = confSchemaBody;
    }
    public byte[] getConfDeltaBody() {
        return confDeltaBody;
    }
    public void setConfDeltaBody(byte[] confDeltaBody) {
        this.confDeltaBody = confDeltaBody;
    }
    public int getNotifAppStateSeqNumber() {
        return notifAppStateSeqNumber;
    }
    public void setNotifAppStateSeqNumber(int notifAppStateSeqNumber) {
        this.notifAppStateSeqNumber = notifAppStateSeqNumber;
    }
    public SyncResponseStatus getNotifResponseStatus() {
        return notifResponseStatus;
    }
    public void setNotifResponseStatus(SyncResponseStatus notifResponseStatus) {
        this.notifResponseStatus = notifResponseStatus;
    }
    public List<Notification> getNotifications() {
        return notifications;
    }
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
    public List<Topic> getNotifAvailableTopics() {
        return notifAvailableTopics;
    }
    public void setNotifAvailableTopics(List<Topic> notifAvailableTopics) {
        this.notifAvailableTopics = notifAvailableTopics;
    }
    public UserAttachResponse getUserAttachResponse() {
        return userAttachResponse;
    }
    public void setUserAttachResponse(UserAttachResponse userAttachResponse) {
        this.userAttachResponse = userAttachResponse;
    }
    public UserAttachNotification getUserAttachNotification() {
        return userAttachNotification;
    }
    public void setUserAttachNotification(UserAttachNotification userAttachNotification) {
        this.userAttachNotification = userAttachNotification;
    }
    public UserDetachNotification getUserDetachNotification() {
        return userDetachNotification;
    }
    public void setUserDetachNotification(UserDetachNotification userDetachNotification) {
        this.userDetachNotification = userDetachNotification;
    }
    public List<EndpointAttachResponse> getUserEndpointAttachResponses() {
        return userEndpointAttachResponses;
    }
    public void setUserEndpointAttachResponses(List<EndpointAttachResponse> userEndpointAttachResponses) {
        this.userEndpointAttachResponses = userEndpointAttachResponses;
    }
    public List<EndpointDetachResponse> getUserEndpointDetachResponses() {
        return userEndpointDetachResponses;
    }
    public void setUserEndpointDetachResponses(List<EndpointDetachResponse> userEndpointDetachResponses) {
        this.userEndpointDetachResponses = userEndpointDetachResponses;
    }
    public EventSequenceNumberResponse getEventSequenceNumberResponse() {
        return eventSequenceNumberResponse;
    }
    public void setEventSequenceNumberResponse(EventSequenceNumberResponse eventSequenceNumberResponse) {
        this.eventSequenceNumberResponse = eventSequenceNumberResponse;
    }
    public List<EventListenersResponse> getEventListenersResponses() {
        return eventListenersResponses;
    }
    public void setEventListenersResponses(List<EventListenersResponse> eventListenersResponses) {
        this.eventListenersResponses = eventListenersResponses;
    }
    public List<JsonEvent> getEvents() {
        return events;
    }
    public void setEvents(List<JsonEvent> events) {
        this.events = events;
    }
    public int getRedirectAccessPointId() {
        return redirectAccessPointId;
    }
    public void setRedirectAccessPointId(int redirectAccessPointId) {
        this.redirectAccessPointId = redirectAccessPointId;
    }
    public List<LogDeliveryStatus> getLogDeliveryStatuses() {
        return logDeliveryStatuses;
    }
    public void setLogDeliveryStatuses(List<LogDeliveryStatus> logDeliveryStatuses) {
        this.logDeliveryStatuses = logDeliveryStatuses;
    }

}

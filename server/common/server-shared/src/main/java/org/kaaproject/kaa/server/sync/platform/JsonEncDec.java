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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
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

    public JsonEncDec() {
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
            return jsonMapper.writeValueAsString(wrapServer(sync)).getBytes(Charset.forName("UTF-8"));
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
        } else if (source.getEndpointID() != null && !source.getEndpointID().isEmpty()) {
            metadata.setEndpointPublicKeyHash(generateEndointKey(source.getEndpointID()));
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
        } else {
            sync.setProfileSync(generateDefaultProfile());
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
            LogClientSync logSync = new LogClientSync(source.getLogsSync(), source.getLogReqId());
            sync.setLogSync(logSync);
        }

        return sync;
    }

    private ServerSyncLight wrapServer(ServerSync source) throws PlatformEncDecException {
        ServerSyncLight sync = new ServerSyncLight();
        sync.setRequestId(source.getRequestId());
        sync.setStatus(source.getStatus());
        if (source.getBootstrapSync() != null) {
            sync.setBootstrapProtocolList(source.getBootstrapSync().getProtocolList());
            sync.setBootstrapRequestId(source.getBootstrapSync().getRequestId());
        }
        if (source.getProfileSync() != null) {
            sync.setProfileSync(source.getProfileSync().getResponseStatus());
        }
        if (source.getConfigurationSync() != null) {
            sync.setConfAppStateSeqNumber(source.getConfigurationSync().getAppStateSeqNumber());
            sync.setConfResponseStatus(source.getConfigurationSync().getResponseStatus());
            if (source.getConfigurationSync().getConfDeltaBody() != null) {
                sync.setConfDeltaBody(source.getConfigurationSync().getConfDeltaBody().array());
            }
            if (source.getConfigurationSync().getConfSchemaBody() != null) {
                sync.setConfSchemaBody(source.getConfigurationSync().getConfSchemaBody().array());
            }
        }
        if (source.getNotificationSync() != null) {
            sync.setNotifAppStateSeqNumber(source.getNotificationSync().getAppStateSeqNumber());
            sync.setNotifResponseStatus(source.getNotificationSync().getResponseStatus());
            sync.setNotifAvailableTopics(source.getNotificationSync().getAvailableTopics());
            sync.setNotifications(source.getNotificationSync().getNotifications());
        }
        if (source.getUserSync() != null) {
            sync.setUserAttachNotification(source.getUserSync().getUserAttachNotification());
            sync.setUserAttachResponse(source.getUserSync().getUserAttachResponse());
            sync.setUserDetachNotification(source.getUserSync().getUserDetachNotification());
            sync.setUserEndpointAttachResponses(source.getUserSync().getEndpointAttachResponses());
            sync.setUserEndpointDetachResponses(source.getUserSync().getEndpointDetachResponses());
        }
        if (source.getEventSync() != null) {
            sync.setEventSequenceNumberResponse(source.getEventSync().getEventSequenceNumberResponse());
            sync.setEventListenersResponses(source.getEventSync().getEventListenersResponses());
            if (source.getEventSync().getEvents() != null) {
                List<JsonEvent> events = new ArrayList<ClientSyncLight.JsonEvent>();
                for (Event e : source.getEventSync().getEvents()) {
                    byte[] eventArray = null;
                    if (e.getEventData() != null) {
                        eventArray = e.getEventData().array();
                    }
                    events.add(new JsonEvent(e.getSeqNum(), e.getEventClassFQN(), eventArray, e.getSource(), e
                            .getTarget()));
                }
                sync.setEvents(events);
            }
        }
        if (source.getRedirectSync() != null) {
            sync.setRedirectAccessPointId(source.getRedirectSync().getAccessPointId());
        }
        if (source.getLogSync() != null) {
            sync.setLogDeliveryStatuses(source.getLogSync().getDeliveryStatuses());
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

    private ByteBuffer generateEndointKey(String endpointID) throws PlatformEncDecException {
        return ByteBuffer.wrap(Base64.decodeBase64(DigestUtils.sha1Hex(endpointID).getBytes()));
    }

    private ProfileClientSync generateDefaultProfile() throws PlatformEncDecException {
        // Valid key, generated by KeyUtils, used as stub for sync without
        // pofileClientSync
        byte[] defaultEndpointKey = { 48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3,
                -126, 1, 15, 0, 48, -126, 1, 10, 2, -126, 1, 1, 0, -121, 72, -59, -108, 90, 20, 4, 65, -121, 34, -17,
                -15, -28, -23, -29, 127, -17, 80, 54, 127, 12, 100, 115, -10, 18, -70, 12, -59, 82, 10, 49, 13, 7, 34,
                -119, 87, -128, -101, -104, 72, 121, -105, -55, -29, -16, 43, -118, 85, -95, 102, -82, -47, -108, -105,
                -110, -12, -10, -50, -62, 107, 21, -100, -26, -35, -90, 60, 1, -31, 12, -38, -70, 31, -64, -36, -21,
                -77, -20, -46, -30, 77, -36, -58, 34, 2, 83, 40, 56, -4, 47, 68, 91, 52, -6, -42, -11, 64, -33, 68, -4,
                23, 77, -34, 58, -38, 1, 114, 76, -56, -3, 17, 80, -25, -27, -104, -125, -63, -14, 4, 67, -87, -115,
                -49, 70, 75, 21, -117, -58, -44, -65, 105, -62, -122, -12, 78, -44, -75, 23, 110, -125, 58, -84, -18,
                -114, -10, -77, -126, 102, -79, -22, 95, 24, -118, -44, 68, 69, 33, 1, -125, -81, -22, 30, 62, -54,
                -112, -20, -53, -76, 93, 102, -91, 86, -12, -92, 54, -40, 96, -10, -2, 35, -82, -102, 115, -78, -110,
                -117, -53, 67, 77, -95, 1, 38, 61, -44, -25, 21, 6, -40, -10, -1, 89, -22, -69, -94, -97, -64, 109, 32,
                -66, 111, 95, 88, 92, -13, 125, -61, 123, 50, 31, 90, -61, -67, -116, 111, -85, -94, -77, 110, -88, 41,
                -76, -121, 71, -9, 8, -16, 40, -67, 125, -18, -122, -3, -97, -75, 34, -41, -22, -38, -2, -126, -19, 4,
                -128, -73, -105, -90, -5, 2, 3, 1, 0, 1 };
        return new ProfileClientSync(ByteBuffer.wrap(defaultEndpointKey), ByteBuffer.wrap(new byte[] {}), null, null);
    }

}

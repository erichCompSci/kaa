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
package org.kaaproject.kaa.server.transports.rest.transport;

import java.util.UUID;

import org.kaaproject.kaa.common.Constants;
import org.kaaproject.kaa.server.transport.channel.ChannelContext;
import org.kaaproject.kaa.server.transport.channel.ChannelType;
import org.kaaproject.kaa.server.transport.message.AbstractMessage;
import org.kaaproject.kaa.server.transport.message.ErrorBuilder;
import org.kaaproject.kaa.server.transport.message.MessageBuilder;
import org.kaaproject.kaa.server.transport.message.SessionInitMessage;
import org.kaaproject.kaa.server.transport.session.SessionInfo;

public class RestSyncMessage extends AbstractMessage implements SessionInitMessage {

    /** The signature. */
    private byte[] requestSignature;

    /** The encoded request session key. */
    private byte[] requestKey;

    /** The request data. */
    private byte[] requestData;

    /** The response body. */
    private byte[] responseBody;

    /** The signature. */
    private byte[] responseSignature;

    protected RestSyncMessage(UUID uuid, ChannelContext channelContext, MessageBuilder responseConverter,
            ErrorBuilder errorConverter, byte[] requestData, byte[] requestKey, byte[] requestSignature) {
        super(uuid, Constants.KAA_PLATFORM_PROTOCOL_JSON_ID, channelContext, ChannelType.SYNC, responseConverter,
                errorConverter);
        this.requestData = requestData;
        this.requestKey = requestKey;
        this.requestSignature = requestSignature;
    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public void onSessionCreated(SessionInfo session) {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] getEncodedMessageData() {
        return requestData;
    }

    @Override
    public byte[] getEncodedSessionKey() {
        return requestKey;
    }

    @Override
    public byte[] getSessionKeySignature() {
        return requestSignature;
    }

    @Override
    public int getKeepAlive() {
        // TODO Auto-generated method stub
        return 0;
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public byte[] getResponseSignature() {
        return responseSignature;
    }

    public void setResponseSignature(byte[] responseSignature) {
        this.responseSignature = responseSignature;
    }

}

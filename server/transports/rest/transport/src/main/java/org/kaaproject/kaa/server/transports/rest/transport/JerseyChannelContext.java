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

import javax.ws.rs.container.AsyncResponse;

import org.kaaproject.kaa.server.transport.channel.ChannelContext;

public class JerseyChannelContext implements ChannelContext {
    private final AsyncResponse response;
    StringBuilder responseString = new StringBuilder();

    public JerseyChannelContext(AsyncResponse response) {
        super();
        this.response = response;
    }

    @Override
    public void writeAndFlush(Object response) {
        this.response.resume(new String((byte[]) response));
    }

    @Override
    public void write(Object object) {
        responseString.append(new String(((RestSyncMessage) object).getResponseBody()));
    }

    @Override
    public void flush() {
        response.resume(responseString.toString());
    }

    @Override
    public void fireExceptionCaught(Exception e) {
        response.resume("Request failed, reason is: " + e.toString());
    }

}

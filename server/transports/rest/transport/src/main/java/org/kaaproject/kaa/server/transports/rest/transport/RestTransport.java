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

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.kaaproject.kaa.server.transport.AbstractKaaTransport;
import org.kaaproject.kaa.server.transport.SpecificTransportContext;
import org.kaaproject.kaa.server.transport.TransportLifecycleException;
import org.kaaproject.kaa.server.transport.rest.config.gen.AvroRESTConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestTransport extends AbstractKaaTransport<AvroRESTConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(RestTransport.class);
    private static final int SUPPORTED_VERSION = 1;

    private HttpServer server;

    @Override
    public void start() {
        LOG.info("Starting grizly server");
        try {
            server.start();
        } catch (IOException e) {
            LOG.warn("Server start failed, caused by:", e);
        }
    }

    @Override
    public void stop() {
        LOG.info("Stopping grizly server");
        server.shutdown();
    }

    @Override
    protected void init(SpecificTransportContext<AvroRESTConfig> context) throws TransportLifecycleException {
        AvroRESTConfig configuration = context.getConfiguration();
        configuration.setBindInterface(replaceProperty(configuration.getBindInterface(), BIND_INTERFACE_PROP_NAME,
                context.getCommonProperties().getProperty(BIND_INTERFACE_PROP_NAME, LOCALHOST)));
        configuration.setPublicInterface(replaceProperty(configuration.getPublicInterface(),
                PUBLIC_INTERFACE_PROP_NAME,
                context.getCommonProperties().getProperty(PUBLIC_INTERFACE_PROP_NAME, LOCALHOST)));
        URI baseUri = UriBuilder.fromUri("http://" + configuration.getBindInterface() + "/")
                .port(configuration.getBindPort()).build();
        ResourceConfig config = new RestConfig(this.handler);
        server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }

    @Override
    public Class<AvroRESTConfig> getConfigurationClass() {
        return AvroRESTConfig.class;
    }

    @Override
    protected ByteBuffer getSerializedConnectionInfo() {
        byte[] interfaceData = toUTF8Bytes(context.getConfiguration().getPublicInterface());
        byte[] publicKeyData = context.getServerKey().getEncoded();
        ByteBuffer buf = ByteBuffer.wrap(new byte[SIZE_OF_INT * 3 + interfaceData.length + publicKeyData.length]);
        buf.putInt(publicKeyData.length);
        buf.put(publicKeyData);
        buf.putInt(interfaceData.length);
        buf.put(interfaceData);
        buf.putInt(context.getConfiguration().getPublicPort());
        return buf;
    }

    @Override
    protected int getMinSupportedVersion() {
        return SUPPORTED_VERSION;
    }

    @Override
    protected int getMaxSupportedVersion() {
        return SUPPORTED_VERSION;
    }
}

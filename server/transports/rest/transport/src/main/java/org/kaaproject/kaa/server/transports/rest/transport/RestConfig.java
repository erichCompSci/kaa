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
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.kaaproject.kaa.common.endpoint.CommonEPConstans;
import org.kaaproject.kaa.server.transport.message.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestConfig extends ResourceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(RestConfig.class);

    public RestConfig(final MessageHandler handler) {
        // packages("org.kaaproject.kaa.server.transports.rest.transport.commands");
        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path("/sync");

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("POST");
        methodBuilder.suspended(AsyncResponse.NO_TIMEOUT, TimeUnit.SECONDS)
                .handledBy(new Inflector<ContainerRequestContext, Response>() {
                    @Inject
                    private javax.inject.Provider<AsyncResponse> responseProvider;
                    private UUID uuid = UUID.randomUUID();

                    @Override
                    public Response apply(final ContainerRequestContext context) {
                        final AsyncResponse response = responseProvider.get();
                        Executors.newSingleThreadExecutor().submit(new RestHandler() {
                            RestSyncMessage message;

                            @Override
                            public void run() {
                                try {
                                    message = extractMessage(context);
                                    handler.process(message);
                                } catch (final Exception e) {
                                    LOG.warn("Error occured during request processing: ", e);
                                    response.resume(e);
                                }
                            }

                            private RestSyncMessage extractMessage(ContainerRequestContext context)
                                    throws IllegalArgumentException, IOException {
                                if (!isJson(context))
                                    throw new IllegalArgumentException("Request shoul be of JSON media type");
                                LOG.info("Post request: {}", context.getHeaders());
                                String signature = context
                                        .getHeaderString(CommonEPConstans.REQUEST_SIGNATURE_ATTR_NAME);
                                String key = context.getHeaderString(CommonEPConstans.REQUEST_KEY_ATTR_NAME);
                                if (signature == null)
                                    signature = "";
                                if (key == null)
                                    key = "";
                                return new RestSyncMessage(uuid, new JerseyChannelContext(response), this, this,
                                        IOUtils.toByteArray(context.getEntityStream()), key.getBytes(Charset
                                                .forName("UTF-8")), signature.getBytes(Charset.forName("UTF-8")));
                            }

                            private boolean isJson(ContainerRequestContext request) {
                                return request.getMediaType().toString().contains("application/json");
                            }

                            @Override
                            public Object[] build(byte[] responseData, byte[] responseSignatureData, boolean isEncrypted) {
                                message.setResponseBody(responseData);
                                if (responseSignatureData != null) {
                                    message.setResponseSignature(responseSignatureData);
                                }
                                return new Object[] { message };
                            }

                            @Override
                            public Object[] build(byte[] messageData, boolean isEncrypted) {
                                return build(messageData, null, isEncrypted);
                            }

                            @Override
                            public Object[] build(Exception e) {
                                return null;
                            }
                        });
                        return null;
                    }
                }).build();
        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }
}

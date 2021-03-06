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

package org.kaaproject.kaa.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.Charsets;
import org.kaaproject.kaa.client.channel.GenericTransportInfo;
import org.kaaproject.kaa.client.channel.TransportConnectionInfo;
import org.kaaproject.kaa.client.channel.ServerType;
import org.kaaproject.kaa.client.channel.TransportProtocolId;
import org.kaaproject.kaa.client.util.Base64;
import org.kaaproject.kaa.common.endpoint.gen.ProtocolMetaData;
import org.kaaproject.kaa.common.endpoint.gen.ProtocolVersionPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class to store base endpoint configuration
 */
public class KaaClientProperties extends Properties {
    private static final Logger LOG = LoggerFactory.getLogger(KaaClientProperties.class);

    private static final String DEFAULT_CLIENT_PROPERTIES = "client.properties";
    public static final String KAA_CLIENT_PROPERTIES_FILE = "kaaClientPropertiesFile";

    private static final long serialVersionUID = 8793954229852581418L;

    public static final String BUILD_VERSION = "build.version";
    public static final String BUILD_COMMIT_HASH = "build.commit_hash";
    public static final String TRANSPORT_POLL_DELAY = "transport.poll.initial_delay";
    public static final String TRANSPORT_POLL_PERIOD = "transport.poll.period";
    public static final String TRANSPORT_POLL_UNIT = "transport.poll.unit";
    public static final String BOOTSTRAP_SERVERS = "transport.bootstrap.servers";
    public static final String CONFIG_DATA_DEFAULT = "config.data.default";
    public static final String CONFIG_SCHEMA_DEFAULT = "config.schema.default";
    public static final String SDK_TOKEN = "sdk_token";

    private static final String PROPERTIES_HASH_ALGORITHM = "SHA";

    private final Base64 base64;  

    private byte[] propertiesHash;
    
    public KaaClientProperties(Base64 base64, Properties properties) {
        super(properties);
        this.base64 = base64;
    }

    public KaaClientProperties(Base64 base64) throws IOException {
        super(loadProperties());
        this.base64 = base64;
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = null;
        String propertiesLocation = DEFAULT_CLIENT_PROPERTIES;
        if (System.getProperty(KAA_CLIENT_PROPERTIES_FILE) != null) {
            propertiesLocation = System.getProperty(KAA_CLIENT_PROPERTIES_FILE);
        }
        properties = new Properties();
        ClassLoader classLoader = Kaa.class.getClassLoader();
        properties.load(classLoader.getResourceAsStream(propertiesLocation));
        return properties;
    }

    public byte[] getPropertiesHash() {
        if (propertiesHash == null) {
            try {
                MessageDigest digest = MessageDigest.getInstance(PROPERTIES_HASH_ALGORITHM);

                updateDigest(digest, TRANSPORT_POLL_DELAY);
                updateDigest(digest, TRANSPORT_POLL_PERIOD);
                updateDigest(digest, TRANSPORT_POLL_UNIT);
                updateDigest(digest, BOOTSTRAP_SERVERS);
                updateDigest(digest, CONFIG_DATA_DEFAULT);
                updateDigest(digest, CONFIG_SCHEMA_DEFAULT);
                updateDigest(digest, SDK_TOKEN);

                propertiesHash = digest.digest();
            } catch (NoSuchAlgorithmException e) {
                LOG.warn("Failed to calculate hash for SDK properties: {}", e);
            }
        }

        return propertiesHash;
    }

    private void updateDigest(MessageDigest digest, String propertyName) {
        String value = getProperty(propertyName);
        if (value != null) {
            digest.update(value.getBytes());
        }
    }

    public String getBuildVersion() {
        return getProperty(BUILD_VERSION);
    }

    public String getCommitHash() {
        return getProperty(BUILD_COMMIT_HASH);
    }

    public Map<TransportProtocolId, List<TransportConnectionInfo>> getBootstrapServers() throws InvalidKeySpecException,
            NoSuchAlgorithmException {
        return parseBootstrapServers(getProperty(KaaClientProperties.BOOTSTRAP_SERVERS));
    }

    public String getSdkToken() {
        return getProperty(KaaClientProperties.SDK_TOKEN);
    }

    public Integer getPollDelay() {
        return Integer.valueOf(getProperty(KaaClientProperties.TRANSPORT_POLL_DELAY));
    }

    public Integer getPollPeriod() {
        return Integer.valueOf(getProperty(KaaClientProperties.TRANSPORT_POLL_PERIOD));
    }

    public TimeUnit getPollUnit() {
        return TimeUnit.valueOf(getProperty(KaaClientProperties.TRANSPORT_POLL_UNIT));
    }

    private Map<TransportProtocolId, List<TransportConnectionInfo>> parseBootstrapServers(String serversStr)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        Map<TransportProtocolId, List<TransportConnectionInfo>> servers = new HashMap<>();
        String[] serversSplit = serversStr.split(";");

        for (String server : serversSplit) {
            if (server != null && !server.trim().isEmpty()) {
                String[] tokens = server.split(":");
                ProtocolMetaData md = new ProtocolMetaData();
                md.setAccessPointId(Integer.valueOf(tokens[0]));
                md.setProtocolVersionInfo(new ProtocolVersionPair(Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2])));
                md.setConnectionInfo(ByteBuffer.wrap(base64.decodeBase64(tokens[3])));
                TransportProtocolId key = new TransportProtocolId(md.getProtocolVersionInfo().getId(), md.getProtocolVersionInfo()
                        .getVersion());
                List<TransportConnectionInfo> serverList = servers.get(key);
                if (serverList == null) {
                    serverList = new ArrayList<TransportConnectionInfo>();
                    servers.put(key, serverList);
                }
                serverList.add(new GenericTransportInfo(ServerType.BOOTSTRAP, md));
            }
        }
        return servers;
    }

    public byte[] getDefaultConfigData() {
        String config = getProperty(KaaClientProperties.CONFIG_DATA_DEFAULT);
        return (config != null) ? base64.decodeBase64(config.getBytes(Charsets.UTF_8)) : null;
    }

    public byte[] getDefaultConfigSchema() {
        String schema = getProperty(KaaClientProperties.CONFIG_SCHEMA_DEFAULT);
        return (schema != null) ? schema.getBytes(Charsets.UTF_8) : null;
    }

}

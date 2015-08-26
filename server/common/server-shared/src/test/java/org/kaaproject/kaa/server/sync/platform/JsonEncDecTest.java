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

import java.nio.charset.Charset;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kaaproject.kaa.server.common.Base64Util;
import org.kaaproject.kaa.server.sync.ClientSync;
import org.kaaproject.kaa.server.sync.Event;
import org.kaaproject.kaa.server.sync.EventSequenceNumberResponse;
import org.kaaproject.kaa.server.sync.EventServerSync;
import org.kaaproject.kaa.server.sync.LogDeliveryStatus;
import org.kaaproject.kaa.server.sync.LogServerSync;
import org.kaaproject.kaa.server.sync.ProfileServerSync;
import org.kaaproject.kaa.server.sync.ServerSync;
import org.kaaproject.kaa.server.sync.SyncResponseStatus;
import org.kaaproject.kaa.server.sync.SyncStatus;
import org.kaaproject.kaa.server.sync.UserAttachNotification;
import org.kaaproject.kaa.server.sync.UserServerSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonEncDecTest {
    private static final Logger LOG = LoggerFactory.getLogger(JsonEncDecTest.class);

    private static final int SHA_1_LENGTH = 20;
    private static final int MAGIC_NUMBER = 42;

    private JsonEncDec encDec;

    @Before
    public void before() {
        encDec = new JsonEncDec();
    }

    @Test(expected = PlatformEncDecException.class)
    public void testWrong() throws PlatformEncDecException {
        encDec.decode("small".getBytes());
    }

    @Test(expected = PlatformEncDecException.class)
    public void testEmpty() throws PlatformEncDecException {
        encDec.decode("{}".getBytes());
    }

    @Test
    public void testParseMetaDataWithNoOptions() throws PlatformEncDecException {
        String baseString = "{\"requestId\":0,\"sdkToken\":\"12345678900987654321abcdEFGH\",\"endpointPublicKeyHash\":"
                + "\"AAAAKgAAAAAAAAAAAAAAAAAAAAA=\",\"profileHash\":\"AAAAKwAAAAAAAAAAAAAAAAAAAAA=\",\""
                + "confAppStateSeqNumber\":0}";
        ClientSync sync = encDec.decode(baseString.getBytes(Charset.forName("UTF-8")));
        Assert.assertNotNull(sync);
        Assert.assertNotNull(sync.getClientSyncMetaData());
    }

    @Test
    public void testEncodeBasicServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(MAGIC_NUMBER);
        String message = new String(encDec.encode(sync));

        Assert.assertTrue(message.contains("" + MAGIC_NUMBER));
        System.out.println(message);
        LOG.trace(message);
    }

    @Test
    public void testEncodeProfileServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(MAGIC_NUMBER);
        ProfileServerSync pSync = new ProfileServerSync(SyncResponseStatus.RESYNC);
        sync.setProfileSync(pSync);

        String message = new String(encDec.encode(sync));
        System.out.println(message);
        Assert.assertTrue(message.contains(SyncResponseStatus.RESYNC.toString()));
        LOG.trace(message);
    }

    @Test
    public void testEncodeLogServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(MAGIC_NUMBER);
        LogServerSync lSync = new LogServerSync(Collections.singletonList(new LogDeliveryStatus(MAGIC_NUMBER,
                SyncStatus.FAILURE, null)));
        sync.setLogSync(lSync);

        String message = new String(encDec.encode(sync));
        System.out.println(message);
        Assert.assertTrue(message.contains(SyncStatus.FAILURE.toString()));
        LOG.trace(message);
    }

    @Test
    public void testEncodeUserServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(MAGIC_NUMBER);
        UserServerSync uSync = new UserServerSync();
        uSync.setUserAttachNotification(new UserAttachNotification("id", "token"));
        sync.setUserSync(uSync);

        String message = new String(encDec.encode(sync));
        System.out.println(message);
        Assert.assertTrue(message.contains("id"));
        Assert.assertTrue(message.contains("token"));
        LOG.trace(message);
    }

    @Test
    public void testEncodeEventServerSync() throws PlatformEncDecException {
        ServerSync sync = new ServerSync();
        sync.setRequestId(MAGIC_NUMBER);
        EventServerSync eSync = new EventServerSync();
        eSync.setEventSequenceNumberResponse(new EventSequenceNumberResponse(MAGIC_NUMBER));
        Event event = new Event();
        event.setEventClassFQN("fqn");
        event.setSource(Base64Util.encode(new byte[SHA_1_LENGTH]));
        eSync.setEvents(Collections.singletonList(event));
        sync.setEventSync(eSync);

        String json = new String(encDec.encode(sync));

        Assert.assertTrue(json.contains("" + MAGIC_NUMBER));
        Assert.assertTrue(json.contains("fqn"));
    }
}

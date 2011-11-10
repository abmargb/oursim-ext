/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.edu.ufcg.lsd.oursim.fd;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class FixedHeartbeatFDTest {

    private FailureDetector fD;

    @Before
    public void init() {
        this.fD = new FixedPingFailureDetector();
    }

    @Test
    public void testNoPingNoHeartbeat() {
        // no hb, works like a fixed fd
        Assert.assertFalse(fD.isFailed("id", 0));
        fD.registerMonitored("id", 0, 100, 50);

        Assert.assertFalse(fD.isFailed("id", 10));
        Assert.assertFalse(fD.shouldPing("id", 10));
        Assert.assertEquals(10, fD.getIdleTime("id", 10).longValue());
        Assert.assertEquals(40, fD.getTimeToNextPing("id", 10).longValue()); // timeout/2

        Assert.assertFalse(fD.isFailed("id", 80));
        Assert.assertTrue(fD.shouldPing("id", 80));
        Assert.assertEquals(80, fD.getIdleTime("id", 80).longValue());
        // no ping sent, negative time to next ping
        Assert.assertEquals(-30, fD.getTimeToNextPing("id", 80).longValue());

        Assert.assertTrue(fD.isFailed("id", 120));

        fD.releaseMonitored("id");
        Assert.assertFalse(fD.isFailed("id", 140));
        Assert.assertFalse(fD.shouldPing("id", 140));
    }

    @Test
    public void testPingObjects() {
        // no hb, works like a fixed fd
        Assert.assertFalse(fD.isFailed("id", 0));
        fD.registerMonitored("id", 0, 100, 50);

        Assert.assertFalse(fD.isFailed("id", 10));
        Assert.assertFalse(fD.shouldPing("id", 10));
        Assert.assertEquals(10, fD.getIdleTime("id", 10).longValue());
        Assert.assertEquals(40, fD.getTimeToNextPing("id", 10).longValue()); // timeout/2

        Assert.assertFalse(fD.isFailed("id", 50));
        Assert.assertTrue(fD.shouldPing("id", 50));
        Assert.assertEquals(80, fD.getIdleTime("id", 80).longValue());
        Assert.assertEquals(-30, fD.getTimeToNextPing("id", 80).longValue());

        fD.messageSent("id", 90, MessageType.PING);

        Assert.assertTrue(fD.isFailed("id", 120));
        Assert.assertFalse(fD.shouldPing("id", 120));
        Assert.assertEquals(120, fD.getIdleTime("id", 120).longValue());
        Assert.assertEquals(20, fD.getTimeToNextPing("id", 120).longValue());
    }

    @Test
    public void testHeartbeatReceiving() {
        Assert.assertFalse(fD.isFailed("id", 0));
        fD.registerMonitored("id", 0, 100, 50);

        Assert.assertFalse(fD.isFailed("id", 10));
        Assert.assertEquals(10, fD.getIdleTime("id", 10).longValue());
        Assert.assertEquals(40, fD.getTimeToNextPing("id", 10).longValue()); // timeout/2

        fD.messageSent("id", 50, MessageType.PING);

        Assert.assertFalse(fD.isFailed("id", 60));
        Assert.assertEquals(80, fD.getIdleTime("id", 80).longValue());

        fD.messageReceived("id", 90, MessageType.PING);

        Assert.assertFalse(fD.isFailed("id", 120));
        Assert.assertEquals(30, fD.getIdleTime("id", 120).longValue());
    }

}

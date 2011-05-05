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

import java.util.Map;

/**
 * A simple implementation of a ping failure detector
 * based on time slices. Monitored objects are grouped by
 * their expiration time in time slices. When a time slice
 * expire, all the objects of that slice are considered failed.
 * While updating a monitored, this failure detector 
 * always rounds up the time slice to provide a sort of grace period.
 * This implementation relies on the application to set the 
 * timeouts of the monitored objects, and
 * these timeouts remain static.
 */
public class SlicedPingFailureDetector extends AbstractFailureDetector {

    private final long sliceSize;

    protected static final long DEFAULT_SLICE_SIZE = 3000;
    
    /**
     * Creates a SlicedPingFailureDetector with specified 
     * time slice size in milliseconds.
     * 
     * @param sliceSize the time slice size
     */
    public SlicedPingFailureDetector(long sliceSize) {
        this.sliceSize = sliceSize;
    }
    
    /**
     * Create a SlicedPingFailureDetector with default 
     * slice size of 3000 milliseconds.
     */
    public SlicedPingFailureDetector() {
        this(DEFAULT_SLICE_SIZE);
    }
    
    /**
     * Create a SlicedPingFailureDetector from a parameters map
     * @param parameters
     */
    public SlicedPingFailureDetector(Map<String, String> parameters) {
        this(FailureDetectorOptParser.parseLong(
                SlicedPingFailureDetector.DEFAULT_SLICE_SIZE, 
                parameters.get("slice")));
    }
    
    @Override
    public void registerMonitored(String id, long now, long timeout, long pingInterval) {
        Monitored monitored = getMonitored(id);
        boolean newMonitored = monitored == null;
        
        if (newMonitored) {
            monitored = new SlicedMonitored(id);
            monitored.setLastHeard(now);
            monitored.setPingInterval(pingInterval);
            
            addMonitored(monitored);
        }
        monitored.setTimeout(timeout);
        
        if (newMonitored) {
            messageReceived(id, now, MessageType.PING);
        }
    }

    @Override
    public boolean isFailed(String id, long now) {
        SlicedMonitored monitored = (SlicedMonitored) getMonitored(id);
        if (monitored == null) {
            return false;
        }
        
        if (now > monitored.expirationSlice) {
            return true;
        }

        return false;
    }

    private static class SlicedMonitored extends Monitored {
        long expirationSlice;
        
        public SlicedMonitored(String id) {
            super(id);
        }
    }
    
    @Override
    public boolean messageReceived(String id, long now, MessageType type) {
        SlicedMonitored monitored = (SlicedMonitored) getMonitored(id);
        
        if (monitored == null) {
            return false;
        }
        
        long expireTime = roundToInterval(now + monitored.getTimeout());
        monitored.setLastHeard(now);
        
        monitored.expirationSlice = Math.max(
                monitored.expirationSlice, expireTime);
        
        return true;
    }

    private long roundToInterval(long time) {
        // We give a one interval grace period
        return (time / sliceSize + 1) * sliceSize;
    }

    @Override
    public boolean updatePingSample(String id, long lastHbTimestamp,
            long interArrivalMean, long interArrivalStdDev) {
        return messageReceived(id, lastHbTimestamp, MessageType.PING);
    }

}

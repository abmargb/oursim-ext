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
 * Failure detector implementation according to 
 * Chen method, as described in its paper 'On the 
 * Quality of Service of Failure Detectors'. Chen's method 
 * uses the average of the received pings timestamps added 
 * to a safety margin parameter called alpha in order to 
 * estimated the next timeout.
 */
public class ChenFailureDetector extends AbstractFailureDetector {

    protected static final long DEFAULT_ALPHA = 1250;

    /**
     * Sampling window size
     */
    private static final int N = 1000;
    
    private long alpha;
    
    /**
     * Create a ChenFailureDetector with specified alpha parameter
     * @param alpha safety margin parameter
     */
    public ChenFailureDetector(long alpha) {
        this.alpha = alpha;
    }
    
    /**
     * Create a ChenFailureDetector from a parameters map
     * @param parameters
     */
    public ChenFailureDetector(Map<String, String> parameters) {
        this(FailureDetectorOptParser.parseLong(
                ChenFailureDetector.DEFAULT_ALPHA, parameters.get("alpha")));
    }
    
    /**
     * Create a ChenFailureDetector with default values 
     * for the alpha parameter: alpha = 1250 ms
     */
    public ChenFailureDetector() {
        this(DEFAULT_ALPHA);
    }
    
    @Override
    public boolean isFailed(String id, long now) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return false;
        }
        
        if (now > monitored.getLastHeard() + getTimeout(monitored.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePingSample(String id, long lastHbTimestamp, 
            long interArrivalMean, long interArrivalStdDev) {
        ChenMonitored monitored = (ChenMonitored) getMonitored(id);
        
        if (monitored == null) {
            return false;
        }
        
        monitored.sampWindow.clear();
        monitored.sampWindow.addInterArrival(interArrivalMean);
        updateMonitoredTimeout(lastHbTimestamp, monitored);
        monitored.setLastHeard(lastHbTimestamp);
        
        return true;
    }

    private void updateMonitoredTimeout(long now, ChenMonitored monitored) {
        if (monitored.sampWindow.size() > 0) {
            Double eA = calcEA(monitored, now);
            long t = eA.longValue() + alpha;
            monitored.setTimeout(t - now);
        }
    }

    private Double calcEA(ChenMonitored monitored, long now) {
        return now + monitored.sampWindow.getMean();
    }

    @Override
    public boolean messageReceived(String id, long now, MessageType type) {
        ChenMonitored monitored = (ChenMonitored) getMonitored(id);

        if (monitored == null) {
            return false;
        }
        
        if (MessageType.PING.equals(type)) {
            monitored.sampWindow.addPing(now);
            updateMonitoredTimeout(now, monitored);
        }
        
        monitored.setLastHeard(now);
        return true;
    }

    @Override
    public void registerMonitored(String id, long now, long timeout, long pingInterval) {
        Monitored monitored = getMonitored(id);
        
        if (monitored == null) {
            monitored = new ChenMonitored(id);
            
            monitored.setPingInterval(pingInterval);
            monitored.setLastHeard(now);
            monitored.setLastSent(now);
            
            addMonitored(monitored);
        }
        
        monitored.setTimeout(timeout);
    }

    private static class ChenMonitored extends Monitored {

        InterArrivalSamplingWindow sampWindow = new InterArrivalSamplingWindow(N);

        public ChenMonitored(String id) {
            super(id);
        }
    }
    
}

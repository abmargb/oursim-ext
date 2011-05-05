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
 * Failure detector implementation according
 * to the Phi Accrual method, as described in the paper
 * 'The Phi Accrual Failure Detector', by Hayashibara. 
 * This method estimates a phi value from the ping arrival 
 * time sampling window. This phi value indicates a suspicion 
 * level of a certain object. If the phi value exceeds a 
 * threshold defined by the application, the monitored object 
 * is assumed as failed.
 * 
 * An exponential distribution is used to model
 * the sampling window, as described in the article 
 * 'ED FD: Improving the Phi Accrual Failure Detector'. 
 * @see PhiTimeoutEvaluator
 * 
 */
public class PhiAccrualFailureDetector extends AbstractFailureDetector {

    protected static final int DEFAULT_MINWINDOWSIZE = 500;
    protected static final double DEFAULT_THRESHOLD = 2.;
    
    private static final int WINDOW_MAX_SIZE = 1000;

    private final double threshold;
    private final int minWindowSize;

    /**
     * Create a PhiAccrualFailureDetector with default threshold value (2.)  
     * and default minWindowSize (500).
     */
    public PhiAccrualFailureDetector() {
        this(DEFAULT_THRESHOLD, DEFAULT_MINWINDOWSIZE);
    }

    /**
     * Create a PhiAccrualFailureDetector with specified threshold.
     * 
     * @param threshold
     *            for the phi value. When the phi value exceeds this threshold
     *            for a certain monitored, the failure detector considers this
     *            object as failed.
     * @param minWindowSize
     *            the sampling window minimum size for the failure detector to
     *            become active. This lower bound gives the failure detector a
     *            warm-up period.
     */
    public PhiAccrualFailureDetector(double threshold, int minWindowSize) {
        this.threshold = threshold;
        this.minWindowSize = minWindowSize;
    }
    
    /**
     * Create a PhiAccrualFailureDetector from a parameters map
     * @param parameters
     */
    public PhiAccrualFailureDetector(Map<String, String> parameters) {
        this(
                FailureDetectorOptParser.parseDouble(
                        PhiAccrualFailureDetector.DEFAULT_THRESHOLD, 
                        parameters.get("threshold")), 
                FailureDetectorOptParser.parseInt(
                        PhiAccrualFailureDetector.DEFAULT_MINWINDOWSIZE, 
                        parameters.get("minwindowsize")));
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

    private void updateTimeout(PhiAccrualMonitored monitored) {
        long mean = (long) monitored.sampWindow.getMean();
        monitored.setTimeout((long)(
                -Math.log(Math.pow(10, -threshold)) * mean));
    }

    @Override
    public boolean updatePingSample(String id, long lastHbTimestamp,
            long interArrivalMean, long interArrivalStdDev) {
        
        PhiAccrualMonitored monitored = (PhiAccrualMonitored) getMonitored(id);
        
        if (monitored == null) {
            return false;
        }
        
        monitored.setLastHeard(lastHbTimestamp);
        
        monitored.sampWindow.clear();
        monitored.sampWindow.addInterArrival(interArrivalMean);
        
        updateTimeout(monitored);
        
        return true;
    }

    @Override
    public void registerMonitored(String id, long now, long timeout, long pingInterval) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            monitored = new PhiAccrualMonitored(id);
            
            monitored.setLastHeard(now);
            monitored.setLastSent(now);
            monitored.setPingInterval(pingInterval);
            
            addMonitored(monitored);
        }
        
        monitored.setTimeout(timeout);
    }

    private static class PhiAccrualMonitored extends Monitored {

        InterArrivalSamplingWindow sampWindow = new InterArrivalSamplingWindow(WINDOW_MAX_SIZE);
        
        public PhiAccrualMonitored(String id) {
            super(id);
        }

    }

    @Override
    public boolean messageReceived(String id, long now, MessageType type) {
        PhiAccrualMonitored monitored = (PhiAccrualMonitored) getMonitored(id);
        
        if (monitored == null) {
            return false;
        }
        
        if (MessageType.PING.equals(type)) {
            monitored.sampWindow.addPing(now);
            if (monitored.sampWindow.size() > minWindowSize) {
                updateTimeout(monitored);
            }
        }
        
        monitored.setLastHeard(now);
        
        return true;
    }

}

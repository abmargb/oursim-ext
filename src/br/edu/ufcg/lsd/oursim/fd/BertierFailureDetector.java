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
 * Failure detector implementation according to Bertier method, 
 * as described in its paper 'Implementation and performance 
 * evaluation of an adaptable failure detector'. Bertier's method 
 * uses three parameters (gamma, beta and phi) to estimate a dynamic 
 * safety margin, which is added to the estimated next timeout.
 */
public class BertierFailureDetector extends AbstractFailureDetector {

    protected static final long DEFAULT_MODERATIONSTEP = 500;
    protected static final double DEFAULT_PHI = 4;
    protected static final double DEFAULT_BETA = 1;
    protected static final double DEFAULT_GAMMA = 0.1;

    /**
     * Sampling window size
     */
    private static final int N = 1000;

    // Failure detector params
    private final double gamma;
    private final double beta;
    private final double phi;
    private final long moderationStep;

    /**
     * Create a BertierFailureDetector with default parameters values: gamma =
     * 0.1, beta = 1.0, phi = 4.0, moderationStep = 500.
     */
    public BertierFailureDetector() {
        this(DEFAULT_GAMMA, DEFAULT_BETA, DEFAULT_PHI, DEFAULT_MODERATIONSTEP);
    }

    /**
     * Create a BertierFailureDetector with specified gamma, beta and phi
     * parameters.
     * 
     * @param gamma
     *            Represents the importance of the last calculated error on the
     *            estimation of the safety margin.
     * @param beta
     *            Represents the importance of the calculated delay on the
     *            estimation of the safety margin.
     * @param phi
     *            Permits to ponder the variance on the estimation of the safety
     *            margin.
     * @param moderationStep
     *            The step to be added to timeout when a false suspicion is
     *            detected.
     */
    public BertierFailureDetector(double gamma, double beta, double phi, long moderationStep) {
        this.gamma = gamma;
        this.beta = beta;
        this.phi = phi;
        this.moderationStep = moderationStep;
    }
    
    /**
     * Create a BertierFailureDetector from a parameters map
     * @param parameters
     */
    public BertierFailureDetector(Map<String, String> parameters) {

        this(
                FailureDetectorOptParser.parseDouble(
                        BertierFailureDetector.DEFAULT_GAMMA, 
                        parameters.get("gamma")),
                FailureDetectorOptParser.parseDouble(
                        BertierFailureDetector.DEFAULT_BETA, 
                        parameters.get("beta")), 
                FailureDetectorOptParser.parseDouble(
                        BertierFailureDetector.DEFAULT_PHI,
                        parameters.get("phi")),
                FailureDetectorOptParser.parseLong(
                        BertierFailureDetector.DEFAULT_MODERATIONSTEP,
                        parameters.get("moderationstep")));
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
        BertierMonitored monitored = (BertierMonitored) getMonitored(id);
        
        if (monitored == null) {
            return false;
        }
        
        monitored.sampWindow.clear();
        monitored.sampWindow.addInterArrival(interArrivalMean);
        
        updateMonitoredTimeout(lastHbTimestamp, monitored, false);
        monitored.setLastHeard(lastHbTimestamp);
        
        return true;
    }
    
    private void updateMonitoredTimeout(long now, BertierMonitored monitored, boolean failed) {
        if (monitored.sampWindow.size() > 0) {
            
            monitored.error = (double) (now - monitored.eA - monitored.delay);
            monitored.delay = monitored.delay
                    + Math.round(gamma * monitored.error);
            monitored.var = monitored.var + gamma
                    * (Math.abs(monitored.error) - monitored.var);
            monitored.alpha = beta * ((double) monitored.delay) + phi
                    * monitored.var;
            
            monitored.eA = Math.round(calcEA(monitored, now));
            long t = monitored.eA + Math.round(monitored.alpha);
            
            if (failed) {
                monitored.deltaP += moderationStep;
            }
            
            monitored.setTimeout(t - now + monitored.deltaP);
        }
    }
    
    @Override
    public boolean messageReceived(String id, long now, MessageType type) {
        BertierMonitored monitored = (BertierMonitored) getMonitored(id);
        
        if (monitored == null) {
            return false;
        }
        
        if (MessageType.PING.equals(type)) {
            boolean failed = now > monitored.getLastHeard() + getTimeout(monitored.getId());
            monitored.sampWindow.addPing(now);
            updateMonitoredTimeout(now, monitored, failed);
        }
        
        monitored.setLastHeard(now);
        
        return true;
    }

    private Double calcEA(BertierMonitored monitored, long now) {
        return now + monitored.sampWindow.getMean();
    }

    @Override
    public void registerMonitored(String id, long now, long timeout, long pingInterval) {
        BertierMonitored monitored = (BertierMonitored) getMonitored(id);
        if (monitored == null) {
            monitored = new BertierMonitored(id);
            
            monitored.setLastSent(now);
            monitored.setLastHeard(now);
            monitored.setPingInterval(pingInterval);
            monitored.delay = timeout / 4;
            // initialize with estimated arrival
            monitored.eA = now + timeout;
            
            addMonitored(monitored);
        }
        
        monitored.setTimeout(timeout);
    }

    private static class BertierMonitored extends Monitored {

        InterArrivalSamplingWindow sampWindow = new InterArrivalSamplingWindow(N);

        long eA; // estimated arrival

        long deltaP; // moderation parameter
        
        double alpha; // calculated safety margin
        double var; // magnitude between errors
        double error; // error of the last estimation

        long delay; // estimate margin

        public BertierMonitored(String id) {
            super(id);
        }
    }

}

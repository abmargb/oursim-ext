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
 * A simple implementation of a ping failure detector. 
 * This implementation relies on the application to set the 
 * timeouts of the monitored objects, and
 * these timeouts remain static.
 */
public class FixedPingFailureDetector extends AbstractFailureDetector {

    public FixedPingFailureDetector() {}
    
    public FixedPingFailureDetector(Map<String, String> parameters) {
        this();
    }
    
    @Override
    public boolean updatePingSample(String id, long now,
            long interArrivalMean, long interArrivalStdDev) {
        return messageReceived(id, now, MessageType.PING);
    }
    
    @Override
    public void registerMonitored(String id, long now, long timeout, long pingInterval) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            monitored = new Monitored(id);
            monitored.setLastHeard(now);
            monitored.setLastSent(now);
            monitored.setPingInterval(pingInterval);
            
            addMonitored(monitored);
        }
        
        monitored.setTimeout(timeout);
    }

    @Override
    public boolean isFailed(String id, long now) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return false;
        }
        if (now > monitored.getLastHeard() + getTimeout(id)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean messageReceived(String id, long now, MessageType type) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return false;
        }
        
        monitored.setLastHeard(now);
        return true;
    }

}

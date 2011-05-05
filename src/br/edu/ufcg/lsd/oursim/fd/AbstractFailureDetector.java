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

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for storing monitored
 * objects and their respective common operations.
 * 
 * @see Monitored
 */
public abstract class AbstractFailureDetector implements FailureDetector {

    private Map<String, Monitored> monitoreds = new HashMap<String, Monitored>();
    
    protected Monitored getMonitored(String monitoredId) {
        return monitoreds.get(monitoredId);
    }
    
    protected void addMonitored(Monitored monitored) {
        monitoreds.put(monitored.getId(), monitored);
    }
    
    protected boolean containsMonitored(String monitoredId) {
        return monitoreds.containsKey(monitoredId);
    }
    
    protected Monitored removeMonitored(String monitoredId) {
        return monitoreds.remove(monitoredId);
    }
    
    @Override
    public boolean setTimeout(String id, long timeout) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return false;
        }
        monitored.setTimeout(timeout);
        return true;
    }
    
    @Override
    public boolean setPingInterval(String id, long interval) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return false;
        }
        monitored.setPingInterval(interval);
        return true;
    }
    
    @Override
    public Long getTimeout(String id) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return null;
        }
        return monitored.getTimeout();
    }
    
    @Override
    public boolean releaseMonitored(String id) {
        return removeMonitored(id) != null;
    }
    
    @Override
    public Long getIdleTime(String id, long now) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return null;
        }
        return now - monitored.getLastHeard();
    }
    
    @Override
    public Long getTimeToNextPing(String id, long now) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return null;
        }
        return (monitored.getLastSent() + monitored.getPingInterval()) - now;
    }
    
    @Override
    public boolean shouldPing(String id, long now) {
        if (!containsMonitored(id)) {
            return false;
        }
        if (getTimeToNextPing(id, now) <= 0) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean messageSent(String id, long now, MessageType type) {
        Monitored monitored = getMonitored(id);
        if (monitored == null) {
            return false;
        }
        monitored.setLastSent(now);
        return true;
    }
    
}

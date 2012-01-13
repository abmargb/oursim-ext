package br.edu.ufcg.lsd.oursim.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.fd.FailureDetector;
import br.edu.ufcg.lsd.oursim.fd.MessageType;

public class ActiveEntity extends Entity {

	private String id;
	private boolean up;
	private final Map<String, Monitor> monitors = new HashMap<String, Monitor>();
	
	private FailureDetector fd;
	private long pingInterval;
	private long timeout;
	
	private final Map<Class<? extends ActiveEntity>, String> onRecoveryEvents = 
			new HashMap<Class<? extends ActiveEntity>, String>();
	
	private final Map<Class<? extends ActiveEntity>, String> onFailureEvents = 
			new HashMap<Class<? extends ActiveEntity>, String>();
	
	public String getId() {
		return id;
	}
	
	public void addOnRecoveryEvent(Class<? extends ActiveEntity> entityType,
			String eventType) {
		onRecoveryEvents.put(entityType, eventType);
	}
	
	public void addOnFailureEvent(Class<? extends ActiveEntity> entityType,
			String eventType) {
		onFailureEvents.put(entityType, eventType);
	}
	
	public String getOnRecoveryEvent(Class<? extends ActiveEntity> entityType) {
		return onRecoveryEvents.get(entityType);
	}
	
	public String getOnFailureEvent(Class<? extends ActiveEntity> entityType) {
		return onFailureEvents.get(entityType);
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isUp() {
		return up;
	}
	
	public void addMonitor(Monitor monitor, Long time) {
		String monitoredId = monitor.getObject().getId();
		monitors.put(monitoredId, monitor);
		
		if (fd != null) {
			fd.registerMonitored(monitoredId, time, 
					timeout, pingInterval);
		}
	}
	
	public Monitor getMonitor(String id) {
		return monitors.get(id);
	}
	
	public Set<Monitor> getMonitors() {
		return new HashSet<Monitor>(monitors.values());
	}
	
	public void isItAliveSent(String monitored, Long time) {
		if (fd != null) {
			fd.messageSent(monitored, time, MessageType.PING);
		}
	}
	
	public Long getTimeToNextPing(String monitored, Long time) {
		return fd.getTimeToNextPing(monitored, time);
	}

	public void updateStatusReceived(String monitored, Long time) {
		getMonitor(monitored).setUp(true);
		getMonitor(monitored).setCreatingConnection(false);
		if (fd != null) {
			fd.messageReceived(monitored, time, MessageType.PING);
		}
	}

	public boolean isFailed(String monitored, Long time) {
		return fd.isFailed(monitored, time);
	}

	public void release(String monitored) {
		if (fd != null) {
			fd.releaseMonitored(monitored);
		}
		monitors.remove(monitored);
	}
	
	public void setFailureDetector(FailureDetector fd) {
		this.fd = fd;
	}
	
	public void setPingInterval(long pingInterval) {
		this.pingInterval = pingInterval;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ActiveEntity)) {
			return false;
		}
		ActiveEntity otherEntity = (ActiveEntity) obj;
		return id.equals(otherEntity.getId());
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
}

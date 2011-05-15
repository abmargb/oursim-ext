package br.edu.ufcg.lsd.oursim.entities;

import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.fd.FailureDetector;
import br.edu.ufcg.lsd.oursim.fd.MessageType;

public class ActiveEntity extends Entity {

	private String id;
	private boolean up;
	private final Map<String, Monitor> monitors = new HashMap<String, Monitor>();
	
	private FailureDetector fd;
	private long pingInterval;
	private long timeout;
	
	public String getId() {
		return id;
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

	public void isItAliveSent(String monitored, Long time) {
		fd.messageSent(monitored, time, MessageType.PING);
	}
	
	public long getTimeToNextPing(String monitored, Long time) {
		return fd.getTimeToNextPing(monitored, time);
	}

	public void updateStatusReceived(String monitored, Long time) {
		getMonitor(monitored).setUp(true);
		getMonitor(monitored).setCreatingConnection(false);
		fd.messageReceived(monitored, time, MessageType.PING);
	}

	public boolean isFailed(String monitored, Long time) {
		return fd.isFailed(monitored, time);
	}

	public void release(String monitored) {
		fd.releaseMonitored(monitored);
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
}

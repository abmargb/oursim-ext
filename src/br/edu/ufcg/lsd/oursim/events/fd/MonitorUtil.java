package br.edu.ufcg.lsd.oursim.events.fd;

import java.util.Collection;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Monitor;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class MonitorUtil {

	public static void registerMonitored(OurSim ourSim, Long time, 
			String interested, String monitored) {
		registerMonitored(ourSim, time, interested, monitored, false);
	}
	
	public static Monitor registerMonitored(OurSim ourSim, Long time, 
			String interested, String monitored, boolean isUp) {
		
		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		ActiveEntity monitoredObj = ourSim.getGrid().getObject(monitored);
		
		Monitor monitor = new Monitor(
				monitoredObj);
		monitor.setUp(isUp);
		interestedObj.addMonitor(monitor, time);
		
		sendIsItAlive(ourSim, time, interested, monitored);
		
		return monitor;
	}

	public static void checkLiveness(OurSim ourSim, Long time) {

		ourSim.setLivenessCheckSchedule(false);
		
		for (ActiveEntity interestedObj : ourSim.getGrid().getAllObjects()) {
			for (Monitor monitor : interestedObj.getMonitors()) {
				ActiveEntity monitoredObj = monitor.getObject();
				checkLiveness(ourSim, time, interestedObj, monitoredObj);
			}
		}
	}

	public static void checkLiveness(OurSim ourSim, Long time,
			ActiveEntity interestedObj, ActiveEntity monitoredObj) {
		
		String interested = interestedObj.getId();
		String monitored = monitoredObj.getId();
		
		Monitor monitor = interestedObj.getMonitor(monitored);
		
		if (interestedObj.isFailed(monitored, time) && monitor.isUp()) {
			monitor.setUp(false);
			String failureEventType = interestedObj.getOnFailureEvent(
					monitoredObj.getClass());
			
			if (failureEventType != null) {
				Event callbackDownEvent = ourSim.createEvent(
						failureEventType, time, interested, monitored);
				ourSim.addEvent(callbackDownEvent);
			}
		} 
			
		scheduleLivenessCheck(ourSim, time);
		
	}

	public static void scheduleLivenessCheck(OurSim ourSim, Long time) {
		if (!ourSim.isLivenessCheckSchedule()) {
			ourSim.setLivenessCheckSchedule(true);
			Long tolerance = ourSim.getLongProperty(
					Configuration.PROP_LIVENESS_CHECK_INTERVAL);
			ourSim.addEvent(ourSim.createEvent(FailureDetectionEvents.LIVENESS_CHECK, 
					time + tolerance));
		}
	}

	public static void sendIsItAlive(OurSim ourSim, Long time,
			String interested, String monitored) {
		
		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		Monitor monitor = interestedObj.getMonitor(monitored);
		if (monitor == null) {
			return;
		}
		
		Event isItAliveReceivedEvent = ourSim.createEvent(FailureDetectionEvents.IS_IT_ALIVE_RECEIVED, 
				time, interested, monitored, monitor.isCreatingConnection());
		interestedObj.isItAliveSent(monitored, time);
		ourSim.addNetworkEvent(isItAliveReceivedEvent);

		if (ourSim.getBooleanProperty(
				Configuration.PROP_USE_FAILURE_DETECTOR)) {
			Event isItAliveSentEvent = ourSim.createEvent(FailureDetectionEvents.IS_IT_ALIVE_SENT, 
					time + interestedObj.getTimeToNextPing(monitored, time), 
					interested, monitored);
			
			ourSim.addEvent(isItAliveSentEvent);
		}
		
	}

	public static void objectIsUp(OurSim ourSim, String entityId, Long time) {
		
		ActiveEntity object = ourSim.getGrid().getObject(entityId);
		object.setUp(true);
		
		if (ourSim.getBooleanProperty(
				Configuration.PROP_USE_FAILURE_DETECTOR)) {
			return;
		}
		
		Collection<ActiveEntity> allObjects = ourSim.getGrid().getAllObjects();
		for (ActiveEntity interestedEntity : allObjects) {
			Monitor monitor = interestedEntity.getMonitor(object.getId());
			if (monitor == null) {
				continue;
			}
			monitor.setUp(true);
			
			Monitor reverseMonitor = object.getMonitor(interestedEntity.getId());
			if (reverseMonitor == null) {
				reverseMonitor = new Monitor(interestedEntity);
				object.addMonitor(reverseMonitor, time);
			}
			
			reverseMonitor.setUp(true);
			
			String recoveryEventType = interestedEntity.getOnRecoveryEvent(
					object.getClass());
			
			if (recoveryEventType != null) {
				Event callbackAliveEvent = ourSim.createEvent(
						recoveryEventType, time, 
						interestedEntity.getId(), entityId);
				ourSim.addEvent(callbackAliveEvent);
			}
		}
	}
	
	public static void objectIsDown(OurSim ourSim, String entityId, Long time) {
		
		ActiveEntity object = ourSim.getGrid().getObject(entityId);
		object.setUp(false);
		
		if (ourSim.getBooleanProperty(
				Configuration.PROP_USE_FAILURE_DETECTOR)) {
			return;
		}
		
		Collection<ActiveEntity> allObjects = ourSim.getGrid().getAllObjects();
		for (ActiveEntity interestedEntity : allObjects) {
			Monitor monitor = interestedEntity.getMonitor(object.getId());
			if (monitor == null) {
				continue;
			}
			monitor.setUp(false);
			
			String failureEventType = interestedEntity.getOnFailureEvent(
					object.getClass());
			
			if (failureEventType != null) {
				Event callbackDownEvent = ourSim.createEvent(
						failureEventType, time, 
						interestedEntity.getId(), entityId);
				ourSim.addEvent(callbackDownEvent);
			}
		}
	}
	
	public static void releaseMonitored(OurSim ourSim, String interested, String monitored, Long time) {
		
		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		interestedObj.release(monitored);
		
		if (ourSim.getBooleanProperty(
				Configuration.PROP_USE_FAILURE_DETECTOR)) {
			return;
		}
		
		ActiveEntity monitoredObj = ourSim.getGrid().getObject(monitored);
		Monitor monitor = monitoredObj.getMonitor(interested);
		
		String failureEventType = monitoredObj.getOnFailureEvent(
				interestedObj.getClass());
		
		if (monitor != null && failureEventType != null) {
			Event callbackDownEvent = ourSim.createEvent(failureEventType,
					time, monitored, interested);

			callbackDownEvent.setTime(time);
			ourSim.addEvent(callbackDownEvent);
		}
	}
}

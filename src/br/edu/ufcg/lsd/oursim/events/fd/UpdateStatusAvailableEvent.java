package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Monitor;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class UpdateStatusAvailableEvent extends AbstractEvent {

	private final String interested;
	private final String monitored;

	public UpdateStatusAvailableEvent(String interested, String monitored) {
		super(Event.DEF_PRIORITY);
		this.interested = interested;
		this.monitored = monitored;
	}

	@Override
	public void process(OurSim ourSim) {
		
		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		ActiveEntity monitoredObj = ourSim.getGrid().getObject(monitored);
		
		Monitor monitor = interestedObj.getMonitor(monitored);
		if (monitor == null) {
			return;
		}
		
		boolean wasUp = monitor.isUp();
		interestedObj.updateStatusReceived(monitored, getTime());
		
		if (!wasUp) {
			String onRecoveryEventType = interestedObj.getOnRecoveryEvent(
					monitoredObj.getClass());
			
			if (onRecoveryEventType != null) {
				Event callbackAliveEvent = ourSim.createEvent(
						onRecoveryEventType, getTime(), interested, monitored);
				ourSim.addEvent(callbackAliveEvent);
			}
			
			if (ourSim.getBooleanProperty(
					Configuration.PROP_USE_FAILURE_DETECTOR)) {
				MonitorUtil.scheduleLivenessCheck(ourSim, getTime());
			}
		}
	}

}

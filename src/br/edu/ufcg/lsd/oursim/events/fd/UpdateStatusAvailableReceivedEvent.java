package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Monitor;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class UpdateStatusAvailableReceivedEvent extends AbstractEvent {

	private final String interested;
	private final String monitored;

	public UpdateStatusAvailableReceivedEvent(Long time, String interested,
			String monitored) {
		super(time, Event.DEF_PRIORITY, null);
		this.interested = interested;
		this.monitored = monitored;
	}

	@Override
	public void process(OurSim ourSim) {
		
		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		boolean wasUp = interestedObj.getMonitor(monitored).isUp();
		interestedObj.updateStatusReceived(monitored, getTime());
		
		Monitor monitoredRef = interestedObj.getMonitor(monitored);
		
		if (!wasUp) {
			Event callbackAliveEvent = monitoredRef.getCallbackAliveEvent();
			if (callbackAliveEvent != null) {
				callbackAliveEvent.setTime(getTime());
				ourSim.addEvent(callbackAliveEvent);
			}
		}
		
		MonitorUtil.checkLiveness(ourSim, getTime(), interested, monitored);
	}

}

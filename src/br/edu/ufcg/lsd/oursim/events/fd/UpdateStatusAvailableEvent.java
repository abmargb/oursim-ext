package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Monitor;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class UpdateStatusAvailableEvent extends AbstractEvent {

	private final String interested;
	private final String monitored;

	public UpdateStatusAvailableEvent(Long time, String interested,
			String monitored) {
		super(time, Event.DEF_PRIORITY);
		this.interested = interested;
		this.monitored = monitored;
	}

	@Override
	public void process(OurSim ourSim) {
		
		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		Monitor monitor = interestedObj.getMonitor(monitored);
		if (monitor == null) {
			return;
		}
		
		boolean wasUp = monitor.isUp();
		interestedObj.updateStatusReceived(monitored, getTime());
		
		Monitor monitoredRef = monitor;
		
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

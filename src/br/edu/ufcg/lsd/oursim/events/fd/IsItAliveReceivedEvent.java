package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Monitor;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class IsItAliveReceivedEvent extends AbstractEvent {

	private final String interested;
	private final String monitored;
	private final Boolean isCreatingConnection;

	public IsItAliveReceivedEvent(String interested, 
			String monitored, Boolean isCreatingConnection) {
		super(Event.DEF_PRIORITY);
		this.interested = interested;
		this.monitored = monitored;
		this.isCreatingConnection = isCreatingConnection;
	}

	@Override
	public void process(OurSim ourSim) {
		ActiveEntity monitoredObj = ourSim.getGrid().getObject(monitored);
		if (monitoredObj != null && monitoredObj.isUp()) {
			
			Monitor reverseMonitor = monitoredObj.getMonitor(interested);
			if (isCreatingConnection && reverseMonitor == null) {
				reverseMonitor = MonitorUtil.registerMonitored(
						ourSim, getTime(), monitored, 
						interested, true);
			} 
				
			if (reverseMonitor != null && reverseMonitor.isUp()){
				ourSim.addNetworkEvent(ourSim.createEvent(FailureDetectionEvents.UPDATE_STATUS_AVAILABLE, 
						getTime(), interested, monitored));
			}
		}
	}

}

package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Monitor;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class IsItAliveReceivedEvent extends AbstractEvent {

	private final String interested;
	private final String monitored;
	private final boolean first;

	public IsItAliveReceivedEvent(Long time, String interested, String monitored, boolean first) {
		super(time, Event.DEF_PRIORITY, null);
		this.interested = interested;
		this.monitored = monitored;
		this.first = first;
	}

	@Override
	public void process(OurSim ourSim) {
		ActiveEntity monitoredObj = ourSim.getGrid().getObject(monitored);
		if (monitoredObj != null && monitoredObj.isUp()) {
			
			Monitor reverseMonitor = monitoredObj.getMonitor(interested);
			if (first && reverseMonitor == null) {
				MonitorUtil.registerMonitored(ourSim, getTime(), monitored, 
						interested, null, null, true);
			} 
				
			if (reverseMonitor != null && reverseMonitor.isUp()){
				ourSim.addNetworkEvent(new UpdateStatusAvailableReceivedEvent(
						getTime(), interested, monitored));
			}
		}
	}

}

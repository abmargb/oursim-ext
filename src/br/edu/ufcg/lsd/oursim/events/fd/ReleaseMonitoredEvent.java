package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class ReleaseMonitoredEvent extends AbstractEvent {

	private final String interested;
	private final String monitored;

	public ReleaseMonitoredEvent(String interested, String monitored) {
		super(Event.DEF_PRIORITY);
		this.interested = interested;
		this.monitored = monitored;
	}

	@Override
	public void process(OurSim ourSim) {
		MonitorUtil.releaseMonitored(ourSim, interested, monitored, getTime());
	}

}

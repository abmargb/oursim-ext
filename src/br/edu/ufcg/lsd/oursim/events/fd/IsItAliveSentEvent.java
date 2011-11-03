package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class IsItAliveSentEvent extends AbstractEvent {

	private final String interested;
	private final String monitored;

	public IsItAliveSentEvent(String interested, String monitored) {
		super(Event.DEF_PRIORITY);
		this.interested = interested;
		this.monitored = monitored;
	}

	@Override
	public void process(OurSim ourSim) {
		MonitorUtil.sendIsItAlive(ourSim, getTime(), 
				interested, monitored);
	}

}

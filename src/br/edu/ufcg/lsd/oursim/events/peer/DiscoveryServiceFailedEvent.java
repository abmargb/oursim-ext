package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class DiscoveryServiceFailedEvent extends AbstractEvent {

	private final String peerId;

	public DiscoveryServiceFailedEvent(String peerId) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		System.out.println("DS failed at " + getTime());
	}

}

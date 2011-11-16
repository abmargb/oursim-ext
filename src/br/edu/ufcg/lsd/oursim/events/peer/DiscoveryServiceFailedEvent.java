package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class DiscoveryServiceFailedEvent extends AbstractEvent {

	public DiscoveryServiceFailedEvent(String peerId, String dsId) {
		super(Event.DEF_PRIORITY);
	}

	@Override
	public void process(OurSim ourSim) {
	}

}

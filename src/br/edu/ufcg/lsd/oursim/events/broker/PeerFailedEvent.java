package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class PeerFailedEvent extends AbstractEvent {

	public PeerFailedEvent(Long time) {
		super(time, Event.DEF_PRIORITY, null);
	}

	@Override
	public void process(OurSim ourSim) {
		System.out.println("Peer failed at " + getTime());
	}

}

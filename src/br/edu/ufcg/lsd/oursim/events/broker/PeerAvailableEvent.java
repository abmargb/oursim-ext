package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class PeerAvailableEvent extends AbstractEvent {

	private final String brokerId;

	public PeerAvailableEvent(Long time, String brokerId) {
		super(time, Event.DEF_PRIORITY);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.BROKER_LOGIN, getTime(), brokerId));
	}

}

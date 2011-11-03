package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class BrokerFailedEvent extends AbstractEvent {

	private final String peerId;
	private final String brokerId;

	public BrokerFailedEvent(String peerId, String brokerId) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		peer.removeBroker(brokerId);
	}

}

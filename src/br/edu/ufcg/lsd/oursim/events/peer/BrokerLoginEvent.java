package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;

public class BrokerLoginEvent extends AbstractEvent {

	private String brokerId;

	public BrokerLoginEvent(String brokerId) {
		super(Event.DEF_PRIORITY);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		Peer peer = ourSim.getGrid().getObject(broker.getPeerId());
		if (!peer.isUp()) {
			return;
		}
		
		peer.addBroker(brokerId);
		
		ourSim.addNetworkEvent(ourSim.createEvent(BrokerEvents.BROKER_LOGGED, getTime(), brokerId));
	}


}

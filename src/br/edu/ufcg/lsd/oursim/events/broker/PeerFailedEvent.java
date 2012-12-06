package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.List;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class PeerFailedEvent extends AbstractEvent {

	private final String brokerId;

	public PeerFailedEvent(String brokerId, String peerId) {
		super(Event.DEF_PRIORITY);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		
		if (!broker.isUp()) {
			return;
		}
		
		List<BrokerRequest> requests = broker.getRequests();
		for (BrokerRequest request : requests) {
			broker.removeRequest(request.getSpec().getId());
			request.getJob().setRequest(null);
		}
	}

}

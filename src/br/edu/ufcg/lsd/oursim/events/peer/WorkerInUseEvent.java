package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;

public class WorkerInUseEvent extends AbstractEvent {

	private final String workerId;
	private final RequestSpec requestSpec;
	private final String peerId;

	public WorkerInUseEvent(RequestSpec requestSpec, String workerId, String peerId) {
		super(Event.DEF_PRIORITY);
		this.requestSpec = requestSpec;
		this.workerId = workerId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		PeerRequest request = peer.getRequest(requestSpec.getId());

		if (request == null || !request.getAllocatedWorkers().contains(workerId)) {
			return;
		}
		
		ourSim.addNetworkEvent(ourSim.createEvent(BrokerEvents.HERE_IS_WORKER, getTime(), 
				workerId, requestSpec));
	}

}

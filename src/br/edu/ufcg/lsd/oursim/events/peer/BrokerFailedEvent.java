package br.edu.ufcg.lsd.oursim.events.peer;

import java.util.List;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.allocation.WorkerDistributionHelper;

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
		
		if (!peer.isUp()) {
			return;
		}
		
		peer.removeBroker(brokerId);
		List<PeerRequest> requests = peer.getRequests();
		
		for (PeerRequest request : requests) {
			if (request.getSpec().getBrokerId().equals(brokerId)) {
				removeRequest(request, peer, ourSim);
			}
		}
	}

	private void removeRequest(PeerRequest request, Peer peer, OurSim ourSim) {
		
		peer.removeRequest(request.getSpec().getId());
		
		for (String workerId : request.getAllocatedWorkers()) {
			WorkerDistributionHelper.redistributeWorker(getTime(), peer, workerId, ourSim);
		}
		
	}

}

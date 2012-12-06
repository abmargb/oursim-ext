package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.accounting.AccountingHelper;
import br.edu.ufcg.lsd.oursim.events.peer.allocation.WorkerDistributionHelper;

public class FinishRequestEvent extends AbstractEvent {

	private final RequestSpec requestSpec;
	private final String peerId;

	public FinishRequestEvent(String peerId, RequestSpec requestSpec) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
		this.requestSpec = requestSpec;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		PeerRequest request = peer.removeRequest(requestSpec.getId());
		
		if (request == null) {
			return;
		}
		
		for (String workerId : request.getAllocatedWorkers()) {
			WorkerDistributionHelper.redistributeWorker(getTime(), peer, workerId, ourSim);
		}
		
		AccountingHelper.commitReplicaAccountings(peer, 
				requestSpec.getId());
	}

}

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

	public FinishRequestEvent(Long time, String peerId, RequestSpec requestSpec) {
		super(time, Event.DEF_PRIORITY, null);
		this.peerId = peerId;
		this.requestSpec = requestSpec;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		PeerRequest request = peer.removeRequest(requestSpec.getId());
		
		for (String workerId : request.getAllocatedWorkers()) {
			WorkerDistributionHelper.redistributeWorker(getTime(), peer, workerId, ourSim);
		}
		
		AccountingHelper.commitReplicaAccountings(peer, 
				requestSpec.getId());
	}

}

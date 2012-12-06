package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class LocalWorkerFailedEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;

	public LocalWorkerFailedEvent(String peerId, String workerId) {
		super(Event.DEF_PRIORITY);
		this.workerId = workerId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		peer.setWorkerState(workerId, WorkerState.UNAVAILABLE);
		
		Allocation allocation = peer.removeAllocation(workerId);
		if (allocation != null) {
			PeerRequest request = allocation.getRequest();
			
			if (request != null) {
				request.removeAllocatedWorker(workerId);
			}
		}
	}

}

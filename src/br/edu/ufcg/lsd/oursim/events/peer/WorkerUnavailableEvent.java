package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerUnavailableEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;

	public WorkerUnavailableEvent(Long time, String workerId, String peerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.workerId = workerId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
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

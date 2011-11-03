package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerIdleEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;

	public WorkerIdleEvent(String workerId, String peerId) {
		super(Event.DEF_PRIORITY);
		this.workerId = workerId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		peer.setWorkerState(workerId, WorkerState.IDLE);
		peer.addAllocation(new Allocation(workerId, peerId));
	}

}

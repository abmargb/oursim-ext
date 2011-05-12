package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerIdleEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;

	public WorkerIdleEvent(Long time, String workerId, String peerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.workerId = workerId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		peer.setWorkerState(workerId, WorkerState.IDLE);
	}

}

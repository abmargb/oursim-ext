package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerAvailableEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;

	public WorkerAvailableEvent(Long time, String workerId, String peerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.workerId = workerId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = (Peer) ourSim.getGrid().getObject(peerId);
		peer.setWorkerState(workerId, Worker.STATE_IDLE);
	}

}

package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.allocation.WorkerDistributionHelper;

public class DisposeWorkerEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;

	public DisposeWorkerEvent(Long time, String workerId, String peerId) {
		super(time, Event.DEF_PRIORITY);
		this.workerId = workerId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		WorkerDistributionHelper.redistributeWorker(getTime(), peer, workerId, ourSim);
	}

}

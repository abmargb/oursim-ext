package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class LocalWorkerAvailableEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;

	public LocalWorkerAvailableEvent(String peerId, String workerId) {
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
		
		Event setPeerEvent = ourSim.createEvent(
				WorkerEvents.SET_PEER, getTime(), workerId, peerId);
		
		ourSim.addNetworkEvent(setPeerEvent);
	}

}

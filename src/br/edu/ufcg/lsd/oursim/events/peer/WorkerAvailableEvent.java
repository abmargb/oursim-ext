package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerAvailableEvent extends AbstractEvent {

	private final String peerId;
	private final String workerId;

	public WorkerAvailableEvent(String peerId, String workerId) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		boolean isLocal = peer.getWorkersIds().contains(workerId);
		
		Event event = null;
		
		if (isLocal) {
			event = ourSim.createEvent(PeerEvents.LOCAL_WORKER_AVAILABLE, 
					getTime(), peerId, workerId);
		} else {
			event = ourSim.createEvent(PeerEvents.REMOTE_WORKER_AVAILABLE, 
					getTime(), peerId, workerId);
		}
		
		event.process(ourSim);
	}

}

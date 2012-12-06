package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class WorkForPeerEvent extends AbstractEvent {

	private final String consumerPeer;
	private final String workerId;
	private final String providerPeer;

	public WorkForPeerEvent(String consumerPeer, String providerPeer, String workerId) {
		super(Event.DEF_PRIORITY);
		this.consumerPeer = consumerPeer;
		this.providerPeer = providerPeer;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		
		if (!worker.isUp()) {
			return;
		}
		
		CleanWorkerHelper.cleanWorker(getTime(), worker, true, ourSim);

		worker.setRemotePeer(consumerPeer);
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.WORKER_DONATED, 
				getTime(), consumerPeer, providerPeer, workerId));
	}

}

package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;

public class RemoteWorkerFailedEvent extends AbstractEvent {

	private final String consumer;
	private final String worker;

	public RemoteWorkerFailedEvent(String consumer, String worker) {
		super(Event.DEF_PRIORITY);
		this.consumer = consumer;
		this.worker = worker;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(consumer);
		
		Allocation allocation = peer.getAllocation(worker);
		String provider = null;
		
		if (allocation != null) {
			PeerRequest request = allocation.getRequest();
			if (request != null) {
				request.removeAllocatedWorker(worker);
			}
			peer.removeAllocation(worker);
			provider = allocation.getProvider();
		} else {
			provider = peer.removeNotRecoveredRemoteWorker(worker);
		}
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.DISPOSE_REMOTE_WORKER, 
				getTime(), provider, worker));
		
		ourSim.addEvent(ourSim.createEvent(FailureDetectionEvents.RELEASE, getTime(), 
				peer.getId(), worker));
	}

}

package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class RemoteWorkerFailureEvent extends AbstractEvent {

	private final String consumer;
	private final String provider;
	private final RequestSpec requestSpec;
	private final String worker;

	public RemoteWorkerFailureEvent(Long time, String consumer, 
			String provider, RequestSpec requestSpec, String worker) {
		super(time, Event.DEF_PRIORITY, null);
		this.consumer = consumer;
		this.provider = provider;
		this.requestSpec = requestSpec;
		this.worker = worker;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(consumer);
		
		Allocation allocation = peer.getAllocation(worker);
		
		if (allocation == null) {
			return;
		}
		
		PeerRequest request = allocation.getRequest();
		if (request != null) {
			request.removeAllocatedWorker(worker);
		}
		
		peer.removeAllocation(worker);
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.DISPOSE_REMOTE_WORKER, 
				getTime(), allocation.getProvider(), worker));
		
		peer.release(worker);
	}

}

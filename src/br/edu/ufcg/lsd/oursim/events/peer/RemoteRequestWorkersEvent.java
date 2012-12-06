package br.edu.ufcg.lsd.oursim.events.peer;

import java.util.List;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.allocation.AllocationHelper;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class RemoteRequestWorkersEvent extends AbstractEvent {

	private final String consumer;
	private final String provider;
	private final RequestSpec requestSpec;

	public RemoteRequestWorkersEvent(String consumer, 
			String provider, RequestSpec requestSpec) {
		super(Event.DEF_PRIORITY);
		this.consumer = consumer;
		this.provider = provider;
		this.requestSpec = requestSpec;
	}

	@Override
	public void process(OurSim ourSim) {
	
		Peer peer = ourSim.getGrid().getObject(provider);
		if (!peer.isUp()) {
			return;
		}
		
		List<Allocation> allocations = AllocationHelper.getAllocationsForRemoteRequest(
				peer, requestSpec, consumer);
		
		for (Allocation allocation : allocations) {
			dispatchAllocation(peer, allocation, ourSim);
		}
		
	}

	private void dispatchAllocation(Peer peer, Allocation allocation,
			OurSim ourSim) {
		
		allocation.setConsumer(consumer);
		allocation.setRequest(null);
		allocation.setWorkerLocal(true);
		allocation.setConsumerLocal(false);
		allocation.setLastAssign(getTime());
		
		peer.setWorkerState(allocation.getWorker(), WorkerState.IN_USE);
		
		ourSim.addNetworkEvent(ourSim.createEvent(WorkerEvents.WORK_FOR_PEER, 
				getTime(), consumer, provider, allocation.getWorker()));
		
	}

}

package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class DisposeRemoteWorkerEvent extends AbstractEvent {

	private final String workerId;
	private final String provider;
	private final String consumer;

	public DisposeRemoteWorkerEvent(String provider, String consumer, String workerId) {
		super(Event.DEF_PRIORITY);
		this.provider = provider;
		this.consumer = consumer;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(provider);
		if (!peer.isUp()) {
			return;
		}
		
		Allocation allocation = peer.getAllocation(workerId);
		
		if (allocation == null || !consumer.equals(allocation.getConsumer())) {
			return;
		}
		
		ourSim.addNetworkEvent(ourSim.createEvent(WorkerEvents.STOP_WORKING, 
				getTime(), workerId));
		
		allocation.setConsumer(null);
		allocation.setRequest(null);
		allocation.setLastAssign(getTime());
		
		peer.setWorkerState(workerId, WorkerState.IDLE);
	}

}

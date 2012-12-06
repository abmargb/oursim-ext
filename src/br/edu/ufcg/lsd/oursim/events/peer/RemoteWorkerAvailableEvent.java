package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.allocation.WorkerDistributionHelper;

public class RemoteWorkerAvailableEvent extends AbstractEvent {

	private final String consumer;
	private final String worker;

	public RemoteWorkerAvailableEvent(String consumer, String worker) {
		super(Event.DEF_PRIORITY);
		this.consumer = consumer;
		this.worker = worker;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(consumer);
		if (!peer.isUp()) {
			return;
		}
		
		String provider = peer.removeNotRecoveredRemoteWorker(worker);
		
		if (provider == null) {
			return;
		}
		
		Allocation allocation = new Allocation(worker, provider);
		allocation.setWorkerLocal(false);
		
		peer.addAllocation(allocation);
		
		WorkerDistributionHelper.redistributeRemoteWorker(getTime(), peer, worker, ourSim);
	}

}

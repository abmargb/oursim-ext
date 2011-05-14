package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.allocation.WorkerDistributionHelper;

public class RemoteWorkerRecoveryEvent extends AbstractEvent {

	private final String consumer;
	private final String provider;
	private final RequestSpec requestSpec;
	private final String worker;

	public RemoteWorkerRecoveryEvent(Long time, String consumer, 
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
		Allocation allocation = new Allocation(worker, provider);
		allocation.setWorkerLocal(false);
		
		peer.addAllocation(allocation);
		
		WorkerDistributionHelper.redistributeRemoteWorker(getTime(), peer, worker, ourSim);
	}

}

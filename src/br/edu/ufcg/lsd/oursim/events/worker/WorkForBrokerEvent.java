package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class WorkForBrokerEvent extends AbstractEvent {

	private final String consumer;
	private final String workerId;
	private final RequestSpec requestSpec;

	public WorkForBrokerEvent(String consumer, RequestSpec requestSpec, String workerId) {
		super(Event.DEF_PRIORITY);
		this.consumer = consumer;
		this.requestSpec = requestSpec;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		CleanWorkerHelper.cleanWorker(getTime(), worker, false, ourSim);

		worker.setConsumer(consumer);
		String peerId = worker.getPeer();
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.WORKER_IN_USE, 
				getTime(), requestSpec, workerId, peerId));	
		
		if (worker.getRemotePeer() != null) {
			WorkAccounting accounting = new WorkAccounting(worker.getId(), 
					worker.getRemotePeer());
			worker.setCurrentWorkAccounting(accounting);
		}
	}

}

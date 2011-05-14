package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class WorkForBrokerEvent extends AbstractEvent {

	private final String consumer;
	private final String workerId;
	private final RequestSpec requestSpec;

	public WorkForBrokerEvent(Long time, String consumer, RequestSpec requestSpec, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.consumer = consumer;
		this.requestSpec = requestSpec;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		String oldConsumer = worker.getConsumer();
		if (oldConsumer != null) {
			worker.release(oldConsumer);
		}

		worker.setConsumer(consumer);
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.WORKER_ALLOCATED, 
				getTime(), consumer, requestSpec, workerId));
	}

}

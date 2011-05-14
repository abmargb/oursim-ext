package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class WorkForPeerEvent extends AbstractEvent {

	private final String consumer;
	private final String workerId;
	private final RequestSpec requestSpec;
	private final String provider;

	public WorkForPeerEvent(Long time, String consumer, String provider, RequestSpec requestSpec, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.consumer = consumer;
		this.provider = provider;
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
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.WORKER_DONATED, 
				getTime(), consumer, provider, requestSpec, workerId));
	}

}

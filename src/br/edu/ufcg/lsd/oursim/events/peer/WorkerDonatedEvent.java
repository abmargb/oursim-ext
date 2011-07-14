package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerDonatedEvent extends AbstractEvent {

	private final String workerId;
	private final String consumerId;
	private final RequestSpec requestSpec;
	private final String provider;

	public WorkerDonatedEvent(Long time, String consumerId, String provider, RequestSpec requestSpec, String workerId) {
		super(time, Event.DEF_PRIORITY);
		this.consumerId = consumerId;
		this.provider = provider;
		this.requestSpec = requestSpec;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.REMOTE_HERE_IS_WORKER, getTime(), 
				consumerId, provider, requestSpec, workerId));
	}

}

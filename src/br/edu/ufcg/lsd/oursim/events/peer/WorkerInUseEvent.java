package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;

public class WorkerInUseEvent extends AbstractEvent {

	private final String workerId;
	private final RequestSpec requestSpec;

	public WorkerInUseEvent(Long time, RequestSpec requestSpec, String workerId) {
		super(time, Event.DEF_PRIORITY);
		this.requestSpec = requestSpec;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		ourSim.addNetworkEvent(ourSim.createEvent(BrokerEvents.HERE_IS_WORKER, getTime(), 
				workerId, requestSpec));
	}

}

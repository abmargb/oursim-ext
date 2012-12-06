package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerPreemptedEvent extends AbstractEvent {

	private final String workerId;
	private final String brokerId;

	public WorkerPreemptedEvent(String brokerId, String workerId) {
		super(Event.DEF_PRIORITY);
		this.brokerId = brokerId;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		if (!broker.isUp()) {
			return;
		}
		
		Event event = ourSim.createEvent(BrokerEvents.WORKER_FAILED, 
				getTime(), brokerId, workerId);
	
		event.process(ourSim);
	}

}

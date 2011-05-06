package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.job.Request;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerFailedEvent extends AbstractEvent {

	private final Request request;
	private final String workerId;

	public WorkerFailedEvent(Long time, Request request, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.request = request;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		// TODO Auto-generated method stub
		
	}

}

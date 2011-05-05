package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class RequestWorkersEvent extends AbstractEvent {

	private String brokerId;
	private final int workersNeeded;

	public RequestWorkersEvent(Long time, String brokerId, int workersNeeded) {
		super(time, Event.DEF_PRIORITY, null);
		this.brokerId = brokerId;
		this.workersNeeded = workersNeeded;
	}

	@Override
	public void process(OurSim ourSim) {
	
	}

}

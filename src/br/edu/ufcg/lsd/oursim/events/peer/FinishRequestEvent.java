package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.job.Request;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class FinishRequestEvent extends AbstractEvent {

	private final Request request;

	public FinishRequestEvent(Long time, Request request) {
		super(time, Event.DEF_PRIORITY, null);
		this.request = request;
	}

	@Override
	public void process(OurSim ourSim) {

	}

}

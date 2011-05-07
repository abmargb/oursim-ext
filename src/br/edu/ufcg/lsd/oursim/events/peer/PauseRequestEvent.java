package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.job.Request;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class PauseRequestEvent extends AbstractEvent {

	private final Request request;

	public PauseRequestEvent(Long time, Request request) {
		super(time, Event.DEF_PRIORITY, null);
		this.request = request;
	}

	@Override
	public void process(OurSim ourSim) {

	}

}

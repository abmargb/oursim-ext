package br.edu.ufcg.lsd.oursim.acceptance;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.global.HaltCondition;

public class HaltByEventCondition extends HaltCondition {

	private final String eventType;

	public HaltByEventCondition(OurSim ourSim, String eventType) {
		super(ourSim);
		this.eventType = eventType;
	}

	@Override
	public boolean halt(Event lastEvent, OurSim ourSim) {
		return lastEvent.getType().equals(eventType);
	}

}

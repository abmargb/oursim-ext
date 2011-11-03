package br.edu.ufcg.lsd.oursim.events.global;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.PrimaryEvent;

public class HaltEvent extends PrimaryEvent {

	public static final String TYPE = "HALT";
	
	public HaltEvent() {
		super(Event.HIGHER_PRIORITY, null);
	}

	@Override
	public void process(OurSim ourSim) {
		ourSim.halt();
	}

}

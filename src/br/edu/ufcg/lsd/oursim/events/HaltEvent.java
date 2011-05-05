package br.edu.ufcg.lsd.oursim.events;

import br.edu.ufcg.lsd.oursim.OurSim;

public class HaltEvent extends AbstractEvent {

	public static final String TYPE = "HALT";
	
	public HaltEvent(Long time, String data) {
		super(time, Event.HIGHER_PRIORITY, data);
	}

	@Override
	public void process(OurSim ourSim) {
		ourSim.halt();
	}

}

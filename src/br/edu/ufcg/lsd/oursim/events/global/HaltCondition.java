package br.edu.ufcg.lsd.oursim.events.global;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventListener;

public abstract class HaltCondition implements EventListener {

	protected final OurSim ourSim;

	public HaltCondition(OurSim ourSim) {
		this.ourSim = ourSim;
	}
	
	@Override
	public void eventProcessed(Event e) {
		if (halt(e, ourSim)) {
			ourSim.halt();
		}
	}
	
	public abstract boolean halt(Event lastEvent, OurSim ourSim);
	
}

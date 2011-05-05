package br.edu.ufcg.lsd.oursim.queue;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class BlankEvent extends AbstractEvent {

	public BlankEvent(Long time, Integer priority) {
		super(time, priority, null);
	}
	
	public BlankEvent(Long time) {
		super(time, Event.DEF_PRIORITY, null);
	}

	@Override
	public void process(OurSim ourSim) {
		
	}

}

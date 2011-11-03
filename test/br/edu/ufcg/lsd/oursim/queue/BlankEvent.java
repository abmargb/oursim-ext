package br.edu.ufcg.lsd.oursim.queue;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class BlankEvent extends AbstractEvent {

	public BlankEvent(Long time, Integer priority) {
		super(priority);
		setTime(time);
	}
	
	public BlankEvent(Long time) {
		super(Event.DEF_PRIORITY);
		setTime(time);
	}

	@Override
	public void process(OurSim ourSim) {
		
	}

}

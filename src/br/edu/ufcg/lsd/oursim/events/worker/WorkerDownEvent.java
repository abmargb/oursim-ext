package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityDownEvent;

public class WorkerDownEvent extends ActiveEntityDownEvent {

	public WorkerDownEvent(String data) {
		super(Event.DEF_PRIORITY, data);
	}

	@Override
	protected void entityDown(OurSim ourSim) {
	}

}

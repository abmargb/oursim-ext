package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class StopWorkingEvent extends AbstractEvent {

	private final String workerId;

	public StopWorkingEvent(String workerId) {
		super(Event.DEF_PRIORITY);
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		
		if (!worker.isUp()) {
			return;
		}
		
		CleanWorkerHelper.cleanWorker(getTime(), worker, true, ourSim);
	}

}

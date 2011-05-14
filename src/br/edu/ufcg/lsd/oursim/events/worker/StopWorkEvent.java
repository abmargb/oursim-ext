package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class StopWorkEvent extends AbstractEvent {

	private final String workerId;

	public StopWorkEvent(Long time, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		String oldConsumer = worker.getConsumer();
		if (oldConsumer != null) {
			worker.release(oldConsumer);
		}

		worker.setConsumer(null);
	}

}

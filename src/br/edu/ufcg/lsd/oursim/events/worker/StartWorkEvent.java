package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class StartWorkEvent extends AbstractEvent {

	private final Replica replica;
	private final String workerId;

	public StartWorkEvent(Long time, Replica replica, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.replica = replica;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		
		WorkAccounting workAccounting = worker.getCurrentWorkAccounting();
		if (workAccounting != null) {
			workAccounting.setInitCPUtime(getTime());
		}
		
		ourSim.addEvent(ourSim.createEvent(WorkerEvents.SEND_HERE_IS_EXECUTION_RESULT, 
				getTime() + (long)(replica.getTask().getDuration() / worker.getCpu()), 
				replica, workerId));
	}

}

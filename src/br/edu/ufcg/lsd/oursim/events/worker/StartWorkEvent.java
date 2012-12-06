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
	private final String brokerId;

	public StartWorkEvent(Replica replica, String workerId, String brokerId) {
		super(Event.DEF_PRIORITY);
		this.replica = replica;
		this.workerId = workerId;
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		
		if (!worker.isUp()) {
			return;
		}
		
		WorkAccounting workAccounting = worker.getCurrentWorkAccounting();
		if (workAccounting != null) {
			workAccounting.setInitCPUtime(getTime());
		}
		
		ourSim.addEvent(ourSim.createEvent(WorkerEvents.SEND_HERE_IS_EXECUTION_RESULT, 
				getTime() + (long)(replica.getTask().getDuration() / worker.getCpu()), 
				replica, workerId, brokerId));
	}

}

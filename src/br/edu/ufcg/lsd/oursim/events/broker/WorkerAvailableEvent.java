package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.List;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerAvailableEvent extends AbstractEvent {

	private final String workerId;
	private final String brokerId;

	public WorkerAvailableEvent(String brokerId, String workerId) {
		super(Event.DEF_PRIORITY);
		this.brokerId = brokerId;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		workerIsAvailable(broker);
		SchedulerHelper.updateScheduler(ourSim, broker, getTime());
	}

	private void workerIsAvailable(Broker broker) {
		List<Job> jobs = broker.getJobs();
		for (Job job : jobs) {
			if (job.getNotRecoveredWorkers().contains(workerId)) {
				job.workerIsAvailable(workerId);
				break;
			}
		}
	}

}

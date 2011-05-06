package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.Set;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class BrokerScheduleEvent extends AbstractEvent {

	private String brokerId;

	public BrokerScheduleEvent(Long time, String brokerId) {
		super(time, Event.HIGHER_PRIORITY, null);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		for (Job job : broker.getJobs()) {
			schedule(broker, job, ourSim);
			execute(job);
			clean(job);
		}
	}

	private void clean(Job job) {
		// TODO Auto-generated method stub
		
	}

	private void schedule(Broker broker, Job job, OurSim ourSim) {
		boolean scheduling = true;

		while (scheduling) {
			scheduling = false;

			for (Task task : job.getTasks()) {
				if (SchedulerHelper.canSchedule(task, ourSim)) {
					String chosenWorkerId = null; 

					Set<String> availableWorkers = broker.getAvailableWorkers();

					if (!availableWorkers.isEmpty()) {
						chosenWorkerId = availableWorkers.iterator().next();
						if (allocate(broker, task, chosenWorkerId)) {
							scheduling = true;
						}
					}
				}
			}
		}
	}
	
	private boolean allocate(Broker broker, Task task, String chosenWorkerId) {
		broker.workerIsInUse(chosenWorkerId);
		return true;
	}

	private void execute(Job job) {
		// TODO Auto-generated method stub
		
	}

}

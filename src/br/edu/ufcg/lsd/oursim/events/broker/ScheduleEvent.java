package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class ScheduleEvent extends AbstractEvent {

	private String brokerId;

	public ScheduleEvent(String brokerId) {
		super(Event.LOWER_PRIORITY);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		if (!broker.isUp()) {
			return;
		}
		
		for (Job job : broker.getJobs()) {
			schedule(broker, job, ourSim);
			execute(job, ourSim);
			clean(broker, job, ourSim);
		}
	}

	private void clean(Broker broker, Job job, OurSim ourSim) {
		if (SchedulerHelper.isJobSatisfied(job, ourSim)) {
			BrokerRequest request = job.getRequest();
			if (request != null && !request.isPaused()) {
				request.setPaused(true);
				ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.PAUSE_REQUEST, 
						getTime(), request.getSpec(), broker.getPeerId()));
			}
			
			Set<String> deallocateWorkers = new HashSet<String>();
			deallocateWorkers.addAll(job.getAvailableWorkers());
			deallocateWorkers.addAll(job.getNotRecoveredWorkers());
			
			for (String worker : deallocateWorkers) {
				SchedulerHelper.disposeWorker(job, request.getSpec().getId(), broker, 
						worker, ourSim, getTime());
			}
		}
	}

	private void schedule(Broker broker, Job job, OurSim ourSim) {
		boolean scheduling = true;

		while (scheduling) {
			scheduling = false;

			for (Task task : job.getTasks()) {
				if (SchedulerHelper.canSchedule(task, ourSim)) {
					String chosenWorkerId = null; 

					Set<String> availableWorkers = job.getAvailableWorkers();

					if (!availableWorkers.isEmpty()) {
						chosenWorkerId = availableWorkers.iterator().next();
						if (allocate(broker, task, chosenWorkerId, ourSim)) {
							scheduling = true;
						}
					}
				}
			}
		}
		
		broker.setScheduled(false);
	}
	
	private boolean allocate(Broker broker, Task task, String chosenWorkerId, OurSim ourSim) {
		if (!(ExecutionState.RUNNING.equals(task.getJob().getState()) || 
				ExecutionState.UNSTARTED.equals(task.getJob().getState()))) {
			return false;
		}
		
		if (!(ExecutionState.RUNNING.equals(task.getState()) || 
				ExecutionState.UNSTARTED.equals(task.getState()))) {
			return false;
		}
		
		if (!SchedulerHelper.canSchedule(task, ourSim)) {
			return false;
		}
		
		task.getJob().workerIsInUse(chosenWorkerId);
		
		Replica replica = new Replica();
		replica.setWorker(chosenWorkerId);
		replica.setTask(task);
		replica.setCreationTime(getTime());
		
		task.addReplica(replica);
		replica.setId(replica.getTask().getReplicas().size());
		
		task.setState(ExecutionState.RUNNING);
		task.getJob().setState(ExecutionState.RUNNING);
		
		return true;
	}

	private void execute(Job job, OurSim ourSim) {
		for (Task task : job.getTasks()) {
			for (Replica replica : task.getReplicas()) {
				if (isReadyToRun(replica)) {
					replica.setState(ExecutionState.RUNNING);
					ourSim.addNetworkEvent(ourSim.createEvent(
							WorkerEvents.START_WORK, getTime(), replica, 
							replica.getWorker(), brokerId));
				}
			}
		}
	}

	private boolean isReadyToRun(Replica replica) {
		return ExecutionState.UNSTARTED.equals(replica.getState())
				&& replica.getWorker() != null;
	}

}

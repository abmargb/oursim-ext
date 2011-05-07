package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class HereIsExecutionResultEvent extends AbstractEvent {

	private final Replica replica;
	private final String brokerId;

	public HereIsExecutionResultEvent(Long time, String brokerId, Replica replica) {
		super(time, Event.DEF_PRIORITY, null);
		this.brokerId = brokerId;
		this.replica = replica;
	}

	@Override
	public void process(OurSim ourSim) {
		if (replica.getWorker() == null) {
			return;
		}
		
		replica.setState(ExecutionState.FINISHED);
		replica.getTask().setState(ExecutionState.FINISHED);
		abortSiblingReplicas(ourSim);
		
		Job job = replica.getTask().getJob();
		updateJobState(job);
		
		executionEnded(ourSim, replica);
	}

	private void executionEnded(OurSim ourSim, Replica finishedReplica) {
		Job job = finishedReplica.getTask().getJob();
		
		String worker = finishedReplica.getWorker();
		finishedReplica.setWorker(null);
		job.workerIsAvailable(worker);
		
		Broker broker = ourSim.getGrid().getObject(brokerId);
		
		if (SchedulerHelper.isJobSatisfied(job, ourSim)) {
			SchedulerHelper.disposeWorker(job, broker, worker, 
					ourSim, getTime());
		}
		
		if (ExecutionState.FINISHED.equals(job.getState())) {
			SchedulerHelper.finishJob(job, broker, ourSim, getTime());
		}
		
		SchedulerHelper.updateScheduler(ourSim, broker, getTime());
	}

	private void abortSiblingReplicas(OurSim ourSim) {
		for (Replica siblingReplica : replica.getTask().getReplicas()) {
			if (ExecutionState.UNSTARTED.equals(siblingReplica.getState()) || 
					ExecutionState.RUNNING.equals(siblingReplica.getState())) {
				siblingReplica.setState(ExecutionState.ABORTED);
				executionEnded(ourSim, siblingReplica);
			}
		}
	}

	private void updateJobState(Job job) {
		boolean finished = true;
		for (Task task : job.getTasks()) {
			if (!ExecutionState.FINISHED.equals(task.getState())) {
				finished = false;
				break;
			}
		}
		if (finished) {
			job.setState(ExecutionState.FINISHED);
		}
	}

}

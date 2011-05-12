package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class HereIsExecutionResultEvent extends AbstractEvent {

	private final Replica replica;

	public HereIsExecutionResultEvent(Long time, Replica replica) {
		super(time, Event.DEF_PRIORITY, null);
		this.replica = replica;
	}

	@Override
	public void process(OurSim ourSim) {
		if (replica.getWorker() == null) {
			return;
		}
		
		replica.setState(ExecutionState.FINISHED);
		replica.setEndTime(getTime());
		ourSim.getTraceCollector().replicaEnded(getTime(), replica);
		
		replica.getTask().setState(ExecutionState.FINISHED);
		replica.getTask().setEndTime(getTime());
		ourSim.getTraceCollector().taskEnded(getTime(), replica.getTask());
		
		abortSiblingReplicas(ourSim);
		
		Job job = replica.getTask().getJob();
		updateJobState(job, ourSim);
		
		SchedulerHelper.executionEnded(replica, ourSim, getTime());
	}

	private void abortSiblingReplicas(OurSim ourSim) {
		for (Replica siblingReplica : replica.getTask().getReplicas()) {
			if (ExecutionState.UNSTARTED.equals(siblingReplica.getState()) || 
					ExecutionState.RUNNING.equals(siblingReplica.getState())) {
				siblingReplica.setState(ExecutionState.ABORTED);
				siblingReplica.setEndTime(getTime());
				ourSim.getTraceCollector().replicaEnded(getTime(), siblingReplica);
				
				SchedulerHelper.executionEnded(siblingReplica, ourSim, getTime());
			}
		}
	}

	private void updateJobState(Job job, OurSim ourSim) {
		boolean finished = true;
		for (Task task : job.getTasks()) {
			if (!ExecutionState.FINISHED.equals(task.getState())) {
				finished = false;
				break;
			}
		}
		if (finished) {
			job.setState(ExecutionState.FINISHED);
			job.setEndTime(getTime());
			ourSim.getTraceCollector().jobEnded(getTime(), job);
		}
	}

}

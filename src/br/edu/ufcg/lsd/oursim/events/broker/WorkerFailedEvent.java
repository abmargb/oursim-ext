package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Request;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.ResumeRequestEvent;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class WorkerFailedEvent extends AbstractEvent {

	private final Request request;
	private final String workerId;

	public WorkerFailedEvent(Long time, Request request, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.request = request;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(
				request.getBrokerId());
		
		if (request.getJob().getInUseWorkers().contains(workerId)) {
			replicaFailed(broker, getReplica(), ourSim);
		}
	}

	private void replicaFailed(Broker broker, Replica replica, OurSim ourSim) {
		replica.setState(ExecutionState.FAILED);
		updateState(replica.getTask(), ourSim);
		updateJobState(replica);
		
		Job job = replica.getTask().getJob();
		
		replica.setWorker(null);
		SchedulerHelper.disposeWorker(job, broker, 
				replica.getWorker(), ourSim, getTime());
		
		
		boolean jobEnded = ExecutionState.FAILED.equals(job.getState());
		if (jobEnded) {
			SchedulerHelper.finishJob(job, broker, ourSim, getTime());
		} else if (!SchedulerHelper.isJobSatisfied(job, ourSim)) {
			ourSim.addNetworkEvent(new ResumeRequestEvent(getTime(), 
					request));
		}
		
		SchedulerHelper.updateScheduler(ourSim, broker, getTime());
	}

	private void updateJobState(Replica replica) {
		if (ExecutionState.FAILED.equals(replica.getTask())) {
			replica.getTask().getJob().setState(ExecutionState.FAILED);
		}
	}

	private void updateState(Task task, OurSim ourSim) {
		int fails = 0;
		for (Replica replica : task.getReplicas()) {
			if (ExecutionState.FAILED.equals(replica.getState())) {
				fails++;
			}
		}
		if (fails >= ourSim.getLongProperty(Configuration.PROP_BROKER_MAX_FAILS)) {
			task.setState(ExecutionState.FAILED);
		}
	}

	private Replica getReplica() {
		for (Task task : request.getJob().getTasks()) {
			for (Replica replica : task.getReplicas()) {
				if (workerId.equals(replica.getWorker())) {
					return replica;
				}
			}
		}
		return null;
	}

}

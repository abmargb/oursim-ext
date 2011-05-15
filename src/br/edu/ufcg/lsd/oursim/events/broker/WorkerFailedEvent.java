package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.accounting.ReplicaAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class WorkerFailedEvent extends AbstractEvent {

	private final BrokerRequest request;
	private final String workerId;

	public WorkerFailedEvent(Long time, BrokerRequest request, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.request = request;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(
				request.getSpec().getBrokerId());
		
		if (request.getJob().getInUseWorkers().contains(workerId)) {
			replicaFailed(broker, getReplica(), ourSim);
		}
		SchedulerHelper.disposeWorker(request.getJob(), broker, 
				workerId, ourSim, getTime());
	}

	private void replicaFailed(Broker broker, Replica replica, OurSim ourSim) {
		
		replica.setState(ExecutionState.FAILED);
		replica.setEndTime(getTime());
		ourSim.getTraceCollector().replicaEnded(getTime(), replica);
		
		updateTaskState(replica.getTask(), ourSim);
		updateJobState(replica, ourSim);
		
		Job job = replica.getTask().getJob();
		
		replica.setWorker(null);
		
		boolean jobEnded = ExecutionState.FAILED.equals(job.getState());
		if (jobEnded) {
			SchedulerHelper.finishJob(job, broker, ourSim, getTime());
		} else if (!SchedulerHelper.isJobSatisfied(job, ourSim)) {
			ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.RESUME_REQUEST, 
					getTime(), request.getSpec(), broker.getPeerId()));
		}
		
		SchedulerHelper.updateScheduler(ourSim, broker, getTime());
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.REPORT_REPLICA_ACCOUNTING, 
				getTime(), new ReplicaAccounting(request.getSpec().getId(), 
						workerId, broker.getId(), getTime() - replica.getCreationTime(), 
						replica.getState()), broker.getPeerId()));
	}

	private void updateJobState(Replica replica, OurSim ourSim) {
		if (ExecutionState.FAILED.equals(replica.getTask())) {
			Job job = replica.getTask().getJob();
			job.setEndTime(getTime());
			job.setState(ExecutionState.FAILED);
			ourSim.getTraceCollector().jobEnded(getTime(), job);
		}
	}

	private void updateTaskState(Task task, OurSim ourSim) {
		int fails = 0;
		for (Replica replica : task.getReplicas()) {
			if (ExecutionState.FAILED.equals(replica.getState())) {
				fails++;
			}
		}
		if (fails >= ourSim.getLongProperty(Configuration.PROP_BROKER_MAX_FAILS)) {
			task.setState(ExecutionState.FAILED);
			task.setEndTime(getTime());
			ourSim.getTraceCollector().taskEnded(getTime(), task);
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

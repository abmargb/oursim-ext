package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.List;

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

	private final String workerId;
	private final String brokerId;

	public WorkerFailedEvent(String brokerId, String workerId) {
		super(Event.DEF_PRIORITY);
		this.brokerId = brokerId;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		if (!broker.isUp()) {
			return;
		}
		
		BrokerRequest request = getRequest(broker, workerId);
		if (request == null) {
			return;
		}
		
		if (request.getJob().hasWorker(workerId)) {
			replicaFailed(broker, getReplica(request), request, ourSim);
		}
		
		SchedulerHelper.disposeWorker(request.getJob(), request.getSpec().getId(), broker, 
				workerId, ourSim, getTime());
	}

	private void replicaFailed(Broker broker, Replica replica, BrokerRequest request, OurSim ourSim) {
		
		Job job = request.getJob();
		
		boolean wasJobEnded = SchedulerHelper.isJobEnded(job);
		
		if (replica != null) {
			replica.setState(ExecutionState.FAILED);
			replica.setEndTime(getTime());
			ourSim.getTraceCollector().replicaEnded(getTime(), replica, broker.getId());
			
			updateTaskState(broker.getId(), replica.getTask(), ourSim);
			updateJobState(broker.getId(), replica, ourSim);
			
			replica.setWorker(null);
		}
		
		if (!wasJobEnded) {
			boolean jobEnded = ExecutionState.FAILED.equals(job.getState());
			if (jobEnded) {
				SchedulerHelper.finishJob(job, broker, ourSim, getTime());
			} else if (!SchedulerHelper.isJobSatisfied(job, ourSim)) {
				ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.RESUME_REQUEST, 
						getTime(), request.getSpec(), broker.getPeerId()));
			}
			
			SchedulerHelper.updateScheduler(ourSim, broker, getTime());
		}
		
		if (replica != null) {
			ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.REPORT_REPLICA_ACCOUNTING, 
					getTime(), new ReplicaAccounting(request.getSpec().getId(), 
							workerId, broker.getId(), getTime() - replica.getCreationTime(), 
							replica.getState()), broker.getPeerId()));
		}
		
	}

	private void updateJobState(String brokerId, Replica replica, OurSim ourSim) {
		if (ExecutionState.FAILED.equals(replica.getTask().getJob().getState())) {
			return;
		}
		
		if (ExecutionState.FAILED.equals(replica.getTask().getState())) {
			Job job = replica.getTask().getJob();
			job.setEndTime(getTime());
			job.setState(ExecutionState.FAILED);
			ourSim.getTraceCollector().jobEnded(getTime(), job, brokerId);
		}
	}

	private void updateTaskState(String brokerId, Task task, OurSim ourSim) {
		
		if (ExecutionState.FAILED.equals(task.getState())) {
			return;
		}
		
		int fails = 0;
		for (Replica replica : task.getReplicas()) {
			if (ExecutionState.FAILED.equals(replica.getState())) {
				fails++;
			}
		}
		if (fails >= ourSim.getLongProperty(Configuration.PROP_BROKER_MAX_FAILS)) {
			task.setState(ExecutionState.FAILED);
			task.setEndTime(getTime());
			ourSim.getTraceCollector().taskEnded(getTime(), task, brokerId);
		}
	}

	private Replica getReplica(BrokerRequest request) {
		for (Task task : request.getJob().getTasks()) {
			for (Replica replica : task.getReplicas()) {
				if (workerId.equals(replica.getWorker())) {
					return replica;
				}
			}
		}
		return null;
	}
	
	private BrokerRequest getRequest(Broker broker, String workerId) {

		List<Job> jobs = broker.getJobs();
		for (Job job : jobs) {
			if (job.hasWorker(workerId)) {
				return job.getRequest();
			}
		}
		
		return null;
	}

}

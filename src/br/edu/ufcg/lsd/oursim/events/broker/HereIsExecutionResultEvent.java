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

public class HereIsExecutionResultEvent extends AbstractEvent {

	private final Replica replica;
	private final String brokerId;

	public HereIsExecutionResultEvent(Replica replica, String brokerId) {
		super(Event.DEF_PRIORITY);
		this.replica = replica;
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		
		if (!broker.isUp()) {
			return;
		}
		
		if (replica.getWorker() == null) {
			SchedulerHelper.updateScheduler(ourSim, broker, getTime());
			return;
		}
		
		replica.setState(ExecutionState.FINISHED);
		replica.setEndTime(getTime());
		ourSim.getTraceCollector().replicaEnded(getTime(), replica, brokerId);
		
		replica.getTask().setState(ExecutionState.FINISHED);
		replica.getTask().setEndTime(getTime());
		ourSim.getTraceCollector().taskEnded(getTime(), replica.getTask(), brokerId);
		
		abortSiblingReplicas(ourSim);
		
		Job job = replica.getTask().getJob();
		updateJobState(job, ourSim);
		
		executionEnded(replica, ourSim);
	}

	private void abortSiblingReplicas(OurSim ourSim) {
		for (Replica siblingReplica : replica.getTask().getReplicas()) {
			if (ExecutionState.UNSTARTED.equals(siblingReplica.getState()) || 
					ExecutionState.RUNNING.equals(siblingReplica.getState())) {
				siblingReplica.setState(ExecutionState.ABORTED);
				siblingReplica.setEndTime(getTime());
				ourSim.getTraceCollector().replicaEnded(getTime(), siblingReplica, brokerId);
				
				executionEnded(siblingReplica, ourSim);
			}
		}
	}

	private void updateJobState(Job job, OurSim ourSim) {
		
		
		Boolean useSpeedHack = ourSim.getBooleanProperty(Configuration.PROP_USE_SPEED_HACK);
		
		boolean finished = true;
		
		if (!useSpeedHack) {
			
			for (Task task : job.getTasks()) {
				if (!ExecutionState.FINISHED.equals(task.getState())) {
					finished = false;
					break;
				}
			}
			
		} else {
			
			finished = job.getFinishedTasks().size() == job.getTasks().size();
			
		}
		
		if (finished) {
			job.setState(ExecutionState.FINISHED);
			job.setEndTime(getTime());
			ourSim.getTraceCollector().jobEnded(getTime(), job, brokerId);
		}
	}
	
	private void executionEnded(Replica finishedReplica, OurSim ourSim) {
		
		Job job = finishedReplica.getTask().getJob();
		
		String worker = finishedReplica.getWorker();
		finishedReplica.setWorker(null);
		job.workerIsAvailable(worker);
		
		BrokerRequest request = finishedReplica.getTask().getJob().getRequest();
		String brokerId = request.getSpec().getBrokerId();
		Broker broker = ourSim.getGrid().getObject(brokerId);
		
		if (SchedulerHelper.isJobSatisfied(job, ourSim)) {
			SchedulerHelper.disposeWorker(job, job.getRequest().getSpec().getId(), 
					broker, worker, ourSim, getTime());
		}
		
		if (ExecutionState.FINISHED.equals(job.getState())) {
			SchedulerHelper.finishJob(job, broker, ourSim, getTime());
		}
		
		SchedulerHelper.updateScheduler(ourSim, broker, getTime());
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.REPORT_REPLICA_ACCOUNTING, 
				getTime(), new ReplicaAccounting(request.getSpec().getId(), 
						worker, brokerId, getTime() - finishedReplica.getCreationTime(), 
						finishedReplica.getState()), broker.getPeerId()));
	}

}

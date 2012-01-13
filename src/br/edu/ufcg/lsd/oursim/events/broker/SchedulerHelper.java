package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class SchedulerHelper {

	public static boolean isJobSatisfied(Job job, OurSim ourSim) {
		
		if (isJobEnded(job)) {
			return true;
		}
		
		if (job.getTasks() == null || job.getTasks().isEmpty()) {
			return true;
		}
		
		for (Task task : job.getTasks()) {
			if (canSchedule(task, ourSim)) {
				return false;
			}
		}
		
		return true;
	}

	public static boolean isJobEnded(Job job) {
		return ExecutionState.FINISHED.equals(job.getState()) || 
				ExecutionState.FAILED.equals(job.getState()) || 
				ExecutionState.ABORTED.equals(job.getState()) ||
				ExecutionState.CANCELLED.equals(job.getState());
	}
	
	public static boolean canSchedule(Task task, OurSim ourSim) {
		
		if (!verifyTaskCanBeProcessed(task, ourSim)) {
			return false;
		}
		
		if (!hasRunningReplica(task)) {
			return true;
		}
		
		if (task.getState().equals(ExecutionState.RUNNING)
				&& hasUnallocatedTasks(task.getJob(), ourSim)) {
			return false;
		}

		return verifyRunningReplica(task, ourSim);
	}
	
	private static boolean hasUnallocatedTasks(Job job, OurSim ourSim) {
		
		Boolean useSpeedHack = ourSim.getBooleanProperty(Configuration.PROP_USE_SPEED_HACK);
		
		if (!useSpeedHack) {
			
			for (Task task : job.getTasks()) {
				if (task.getState().equals(ExecutionState.UNSTARTED)
						|| (!task.getState().equals(ExecutionState.FINISHED) && !hasRunningReplica(task))) {
					return true;
				}
			}
			
			return false;
			
		} else {
			
			return job.getUnallocatedTasks().size() > 0;
		}
		
	}

	private static boolean hasRunningReplica(Task task) {
		
		for (Replica replica : task.getReplicas()) {
			if (ExecutionState.RUNNING.equals(replica.getState())
					|| ExecutionState.UNSTARTED.equals(replica.getState())) {
				return true;
			}
		}

		return false;
	}

	private static boolean verifyTaskCanBeProcessed(Task task, OurSim ourSim) {
		
		int maxFails = ourSim.getIntProperty(Configuration.PROP_BROKER_MAX_FAILS);
		
		if (getFails(task) >= maxFails || 
				hasFinishedReplica(task) || ExecutionState.CANCELLED.equals(task.getState())) {
			return false;
		}
		
		return true;
	}
	
	
	
	private static int getFails(Task task) {
		int fails = 0;
		for (Replica replica : task.getReplicas()) {
			if (ExecutionState.FAILED.equals(replica.getState())) {
				fails++;
			}
		}
		
		return fails;
	}

	private static boolean hasFinishedReplica(Task task) {
		for (Replica replica : task.getReplicas()) {
			if (ExecutionState.FINISHED.equals(replica.getState())) {
				return true;
			}
		}
		
		return false;
	}

	private static boolean verifyRunningReplica(Task task, OurSim ourSim) {
		
		int running = 0;
		
		for (Replica replica : task.getReplicas()) {
			ExecutionState replicaState = replica.getState();

			if (ExecutionState.RUNNING.equals(replicaState) || ExecutionState.UNSTARTED.equals(replicaState)) {
				running++;
			}
		}
		
		int maxReplicas = ourSim.getIntProperty(Configuration.PROP_BROKER_MAX_REPLICAS);
		int maxSimultaneousReplicas = ourSim.getIntProperty(Configuration.PROP_BROKER_MAX_SIMULTANEOUS_REPLICAS);
		
		return task.getReplicas().size() < maxReplicas && running < maxSimultaneousReplicas
			&& (ExecutionState.RUNNING.equals(task.getState()) || ExecutionState.UNSTARTED.equals(task.getState()));
		
	}

	public static void disposeWorker(Job job, long requestId, Broker broker, String worker, OurSim ourSim, long now) {
		
		if (job != null) {
			job.removeWorker(worker);
		}
		
		ourSim.addEvent(ourSim.createEvent(FailureDetectionEvents.RELEASE, now, 
				broker.getId(), worker));
		
		if (broker.getPeerId() != null) {
			ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.DISPOSE_WORKER, now, 
					worker, requestId, broker.getPeerId()));
		}
	}

	public static void finishJob(Job job, Broker broker, OurSim ourSim, long time) {
		Set<String> deallocateWorkers = new HashSet<String>();
		deallocateWorkers.addAll(job.getAvailableWorkers());
		deallocateWorkers.addAll(job.getNotRecoveredWorkers());
		
		for (String worker : deallocateWorkers) {
			disposeWorker(job, job.getRequest().getSpec().getId(), 
					broker, worker, ourSim, time);
		}
		
		if (broker.getPeerId() != null) {
			ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.FINISH_REQUEST, time, 
					broker.getPeerId(), job.getRequest().getSpec()));
		}
	}

	public static void updateScheduler(OurSim ourSim, Broker broker, long now) {
		if (!broker.isScheduled()) {
			broker.setScheduled(true);
			ourSim.addEvent(ourSim.createEvent(BrokerEvents.SCHEDULE, 
					now + ourSim.getLongProperty(Configuration.PROP_BROKER_SCHEDULER_INTERVAL),
					broker.getId()));
		}
	}

}

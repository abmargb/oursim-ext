package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.events.peer.DisposeWorkerEvent;
import br.edu.ufcg.lsd.oursim.events.peer.FinishRequestEvent;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class SchedulerHelper {

	public static boolean isJobSatisfied(Job job, OurSim ourSim) {
		
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
	
	public static boolean canSchedule(Task task, OurSim ourSim) {
		
		if (!verifyTaskCanBeProcessed(task, ourSim)) {
			return false;
		}
		
		if (!hasRunningReplica(task)) {
			return true;
		}
		
		if (task.getState().equals(ExecutionState.RUNNING)
				&& hasUnallocatedTasks(task.getJob())) {
			return false;
		}

		return verifyRunningReplica(task, ourSim);
	}
	
	private static boolean hasUnallocatedTasks(Job job) {
		for (Task task : job.getTasks()) {
			if (task.getState().equals(ExecutionState.UNSTARTED)
					|| (!task.getState().equals(ExecutionState.FINISHED) && !hasRunningReplica(task))) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasRunningReplica(Task task) {
		
		for (Replica replica : task.getReplicas()) {
			if (ExecutionState.RUNNING.equals(replica.getState())) {
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
		
		int maxReplicas = ourSim.getIntProperty(Configuration.PROP_BROKER_MAX_REPLICAS);;
		
		return running < maxReplicas
			&& (ExecutionState.RUNNING.equals(task.getState()) || ExecutionState.UNSTARTED.equals(task.getState()));
		
	}

	public static void disposeWorker(Job job, Broker broker, String worker, OurSim ourSim, long now) {
		job.removeWorker(worker);
		broker.release(worker);
		
		ourSim.addNetworkEvent(new DisposeWorkerEvent(now, 
				worker, broker.getPeerId()));
	}

	public static void finishJob(Job job, Broker broker, OurSim ourSim, long time) {
		for (String worker : job.getAvailableWorkers()) {
			disposeWorker(job, broker, worker, ourSim, time);
		}
		ourSim.addNetworkEvent(new FinishRequestEvent(time, 
				broker.getPeerId(), job.getRequest().getSpec()));
	}

	public static void updateScheduler(OurSim ourSim, Broker broker, long now) {
		if (!broker.isScheduled()) {
			broker.setScheduled(true);
			ourSim.addEvent(new ScheduleEvent(
					now + ourSim.getLongProperty(Configuration.PROP_BROKER_SCHEDULER_INTERVAL),
					broker.getId()));
		}
	}

	public static void executionEnded(Replica finishedReplica, OurSim ourSim, long time) {
		
		Job job = finishedReplica.getTask().getJob();
		
		String worker = finishedReplica.getWorker();
		finishedReplica.setWorker(null);
		job.workerIsAvailable(worker);
		
		BrokerRequest request = finishedReplica.getTask().getJob().getRequest();
		String brokerId = request.getSpec().getBrokerId();
		Broker broker = ourSim.getGrid().getObject(brokerId);
		
		if (isJobSatisfied(job, ourSim)) {
			disposeWorker(job, broker, worker, 
					ourSim, time);
		}
		
		if (ExecutionState.FINISHED.equals(job.getState())) {
			finishJob(job, broker, ourSim, time);
		}
		
		updateScheduler(ourSim, broker, time);
	}
	
}

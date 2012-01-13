package br.edu.ufcg.lsd.oursim.entities.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.entities.ExecutableEntity;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;

public class Job extends ExecutableEntity {

	private BrokerRequest request;
	private Set<String> notRecoveredWorkers = new HashSet<String>();
	private Set<String> availableWorkers = new HashSet<String>();
	private Set<String> inUseWorkers = new HashSet<String>();
	private List<Task> tasks = new ArrayList<Task>();
	
	private Set<Task> finishedTasks = new HashSet<Task>();
	private Set<Task> unallocatedTasks = new HashSet<Task>();
	
	public void addTask(Task task) {
		tasks.add(task);
		unallocatedTasks.add(task);
	}
	
	public Set<Task> getFinishedTasks() {
		return finishedTasks;
	}
	
	public Set<Task> getUnallocatedTasks() {
		return unallocatedTasks;
	}
	
	public void updateTaskState(Task task) {
		switch (task.getState()) {
		case FINISHED:
			finishedTasks.add(task);
		case RUNNING:
		case FAILED: 
		case CANCELLED: 
		case ABORTED:
			unallocatedTasks.remove(task);
			break;
		default:
			break;
		}
	}
	
	public void updateReplicaState(Replica replica) {
		switch (replica.getState()) {
		case FAILED: 
		case CANCELLED: 
		case ABORTED:
			boolean anyRunning = false;
			for (Replica eachReplica : replica.getTask().getReplicas()) {
				if (ExecutionState.RUNNING.equals(eachReplica.getState())) {
					anyRunning = true;
					break;
				}
			}
			if (!anyRunning) {
				unallocatedTasks.add(replica.getTask());
			}
			
			break;
		default:
			break;
		}
	}
	
	public List<Task> getTasks() {
		return tasks;
	}

	public void setRequest(BrokerRequest request) {
		this.request = request;
	}

	public BrokerRequest getRequest() {
		return request;
	}
	
	public void workerIsAvailable(String workerId) {
		availableWorkers.add(workerId);
		inUseWorkers.remove(workerId);
		notRecoveredWorkers.remove(workerId);
	}
	
	public void workerIsNotRecovered(String workerId) {
		availableWorkers.remove(workerId);
		inUseWorkers.remove(workerId);
		notRecoveredWorkers.add(workerId);
	}
	
	public void workerIsInUse(String workerId) {
		availableWorkers.remove(workerId);
		inUseWorkers.add(workerId);
		notRecoveredWorkers.remove(workerId);
	}
	
	public void removeWorker(String workerId){
		availableWorkers.remove(workerId);
		inUseWorkers.remove(workerId);
		notRecoveredWorkers.remove(workerId);
	}
	
	public boolean hasWorker(String workerId) {
		return availableWorkers.contains(workerId)
				|| inUseWorkers.contains(workerId)
				|| notRecoveredWorkers.contains(workerId);
	}

	public Set<String> getAvailableWorkers() {
		return availableWorkers;
	}

	public Set<String> getInUseWorkers() {
		return inUseWorkers;
	}
	
	public Set<String> getNotRecoveredWorkers() {
		return notRecoveredWorkers;
	}
	
}

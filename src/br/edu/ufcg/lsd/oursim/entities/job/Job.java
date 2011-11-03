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
	
	public void addTask(Task task) {
		tasks.add(task);
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

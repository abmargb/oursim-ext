package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.job.Job;

public class Broker extends ActiveEntity {

	private String peerId;
	private List<Job> jobs = new LinkedList<Job>();
	private boolean scheduled;
	private Set<String> availableWorkers = new HashSet<String>();
	private Set<String> inUseWorkers = new HashSet<String>();
	
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	
	public String getPeerId() {
		return peerId;
	}

	public void addJob(Job job) {
		jobs.add(job);
	}
	
	public List<Job> getJobs() {
		return jobs;
	}

	public boolean isScheduled() {
		return scheduled;
	}
	
	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public void workerIsAvailable(String workerId) {
		availableWorkers.add(workerId);
		inUseWorkers.remove(workerId);
	}
	
	public void workerIsInUse(String workerId) {
		availableWorkers.remove(workerId);
		inUseWorkers.add(workerId);
	}
	
	public void removeWorker(String workerId){
		availableWorkers.remove(workerId);
		inUseWorkers.remove(workerId);
	}

	public Set<String> getAvailableWorkers() {
		return availableWorkers;
	}
}

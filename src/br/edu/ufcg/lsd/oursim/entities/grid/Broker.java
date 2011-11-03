package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;

public class Broker extends ActiveEntity {

	private String peerId;
	private List<Job> jobs = new LinkedList<Job>();
	private Map<Long, BrokerRequest> requests = new HashMap<Long, BrokerRequest>();
	private boolean scheduled;
	
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

	public Job getJob(int id) {
		for (Job job : jobs) {
			if (job.getId() == id) {
				return job;
			}
		}
		return null;
	}
	
	public boolean isScheduled() {
		return scheduled;
	}
	
	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}
	
	public BrokerRequest getRequest(long requestId) {
		return requests.get(requestId);
	}
	
	public void addRequest(BrokerRequest request) {
		requests.put(request.getSpec().getId(), request);
	}
	
	public BrokerRequest removeRequest(long requestId) {
		return requests.remove(requestId);
	}
	
	public List<BrokerRequest> getRequests() {
		return new LinkedList<BrokerRequest>(requests.values());
	}

}

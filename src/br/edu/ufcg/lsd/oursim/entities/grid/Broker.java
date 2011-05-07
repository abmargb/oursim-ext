package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.job.Job;

public class Broker extends ActiveEntity {

	private String peerId;
	private List<Job> jobs = new LinkedList<Job>();
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

	public boolean isScheduled() {
		return scheduled;
	}
	
	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

}

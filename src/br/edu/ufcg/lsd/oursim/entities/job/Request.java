package br.edu.ufcg.lsd.oursim.entities.job;

public class Request {

	private long id;
	private Job job;
	private int requiredWorkers;
	private boolean paused;
	private String brokerId;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public int getRequiredWorkers() {
		return requiredWorkers;
	}
	
	public void setRequiredWorkers(int requiredWorkers) {
		this.requiredWorkers = requiredWorkers;
	}
	
	public String getBrokerId() {
		return brokerId;
	}
	
	public void setBrokerId(String brokerId) {
		this.brokerId = brokerId;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Job getJob() {
		return job;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}
	
}

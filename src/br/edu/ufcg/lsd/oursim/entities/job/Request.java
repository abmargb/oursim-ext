package br.edu.ufcg.lsd.oursim.entities.job;

public class Request {

	private long id;
	private long jobId;
	private int requiredWorkers;
	private String brokerId;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getJobId() {
		return jobId;
	}
	
	public void setJobId(long jobId) {
		this.jobId = jobId;
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
	
}

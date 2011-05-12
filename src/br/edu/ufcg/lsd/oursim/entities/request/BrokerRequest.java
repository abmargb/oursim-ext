package br.edu.ufcg.lsd.oursim.entities.request;

import br.edu.ufcg.lsd.oursim.entities.job.Job;

/**
 * Request representation in the Broker
 * @see RequestSpec
 * 
 */
public class BrokerRequest {

	private Job job;
	private boolean paused;
	private final RequestSpec spec;
	
	public BrokerRequest(RequestSpec spec) {
		this.spec = spec;
	}
	
	public void setJob(Job job) {
		this.job = job;
	}

	public RequestSpec getSpec() {
		return spec;
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

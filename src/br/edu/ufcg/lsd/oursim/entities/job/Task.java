package br.edu.ufcg.lsd.oursim.entities.job;

import br.edu.ufcg.lsd.oursim.entities.Entity;

public class Task extends Entity {

	private long duration;
	private State state = State.UNSTARTED;
	private Job job;
	
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDuration() {
		return duration;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public State getState() {
		return state;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Job getJob() {
		return job;
	}
}

package br.edu.ufcg.lsd.oursim.entities.job;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.Entity;

public class Job extends Entity {

	private int jobId;
	private State state = State.UNSTARTED;
	private List<Task> tasks = new ArrayList<Task>();
	
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	public void setId(int jobId) {
		this.jobId = jobId;
	}

	public int getJobId() {
		return jobId;
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
}

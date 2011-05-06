package br.edu.ufcg.lsd.oursim.entities.job;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.Entity;

public class Job extends Entity {

	private int jobId;
	private ExecutionState state = ExecutionState.UNSTARTED;
	private Request request;
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
	
	public ExecutionState getState() {
		return state;
	}
	
	public void setState(ExecutionState state) {
		this.state = state;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}
	
}

package br.edu.ufcg.lsd.oursim.entities.job;

import br.edu.ufcg.lsd.oursim.entities.Entity;

public class Replica extends Entity {

	private ExecutionState state = ExecutionState.UNSTARTED;
	private String workerId;
	private Task task;
	
	public ExecutionState getState() {
		return state;
	}

	public void setWorker(String workerId) {
		this.workerId = workerId;
	}

	public void setState(ExecutionState state) {
		this.state = state;
	}

	public String getWorker() {
		return workerId;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

}

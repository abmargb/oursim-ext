package br.edu.ufcg.lsd.oursim.entities.job;

import br.edu.ufcg.lsd.oursim.entities.ExecutableEntity;

public class Replica extends ExecutableEntity {

	private String workerId;
	private Task task;

	public void setWorker(String workerId) {
		this.workerId = workerId;
	}

	@Override
	public void setState(ExecutionState state) {
		super.setState(state);
		task.getJob().updateReplicaState(this);
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

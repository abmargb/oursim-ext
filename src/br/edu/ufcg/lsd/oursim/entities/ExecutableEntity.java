package br.edu.ufcg.lsd.oursim.entities;

import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;

public class ExecutableEntity extends Entity {

	private ExecutionState state = ExecutionState.UNSTARTED;
	private int id;
	private long creationTime;
	private long endTime;
	
	public ExecutionState getState() {
		return state;
	}
	
	public void setState(ExecutionState state) {
		this.state = state;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
}

package br.edu.ufcg.lsd.oursim.entities.job;

import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.Entity;

public class Task extends Entity {

	private long duration;
	private ExecutionState state = ExecutionState.UNSTARTED;
	private Job job;
	private List<Replica> replicas = new LinkedList<Replica>();
	
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDuration() {
		return duration;
	}
	
	public void setState(ExecutionState state) {
		this.state = state;
	}
	
	public ExecutionState getState() {
		return state;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Job getJob() {
		return job;
	}

	public List<Replica> getReplicas() {
		return replicas;
	}

	public void addReplica(Replica replica) {
		this.replicas.add(replica);
	}
}

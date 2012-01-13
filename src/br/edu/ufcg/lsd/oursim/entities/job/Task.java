package br.edu.ufcg.lsd.oursim.entities.job;

import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.ExecutableEntity;

public class Task extends ExecutableEntity {

	private long duration;
	private Job job;
	private List<Replica> replicas = new LinkedList<Replica>();
	
	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public void setState(ExecutionState state) {
		super.setState(state);
		job.updateTaskState(this);
	}
	
	public long getDuration() {
		return duration;
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

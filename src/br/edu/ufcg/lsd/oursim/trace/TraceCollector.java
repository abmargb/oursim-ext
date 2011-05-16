package br.edu.ufcg.lsd.oursim.trace;

import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;

public interface TraceCollector {

	public void replicaEnded(long time, Replica replica, String brokerId);

	public void jobEnded(long time, Job job, String brokerId);

	public void taskEnded(long time, Task task, String brokerId);
	
}

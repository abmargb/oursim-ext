package br.edu.ufcg.lsd.oursim.trace;

import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;

public interface TraceCollector {

	public void replicaEnded(long time, Replica replica);

	public void jobEnded(long time, Job job);

	public void taskEnded(long time, Task task);
	
}

package br.edu.ufcg.lsd.oursim.acceptance;

import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.trace.TraceCollector;

public class TestTraceCollector implements TraceCollector {

	@Override
	public void replicaEnded(long time, Replica replica, String brokerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobEnded(long time, Job job, String brokerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskEnded(long time, Task task, String brokerId) {
		// TODO Auto-generated method stub
		
	}

}

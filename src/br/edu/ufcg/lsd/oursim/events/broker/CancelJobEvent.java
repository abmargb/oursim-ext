package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.PrimaryEvent;
import br.edu.ufcg.lsd.oursim.util.LineParser;

public class CancelJobEvent extends PrimaryEvent {

	public CancelJobEvent(String data) {
		super(Event.DEF_PRIORITY, data);
	}

	@Override
	public void process(OurSim ourSim) {
		LineParser lineParser = new LineParser(getData());
		String brokerId = lineParser.next();
		String jobId = lineParser.next();
		
		Broker broker = ourSim.getGrid().getObject(brokerId);
		if (broker == null || !broker.isUp()) {
			return;
		}
		
		Job job = broker.getJob(Integer.parseInt(jobId));
		
		if (job == null) {
			return;
		}
		
		cancelJob(job, broker, ourSim);
	}

	private void cancelJob(Job job, Broker broker, OurSim ourSim) {
		for (Task task : job.getTasks()) {
			cancelTask(broker, ourSim, task);
			for (Replica replica : task.getReplicas()) {
				cancelReplica(broker, ourSim, replica);
			}
		}
		
		job.setState(ExecutionState.CANCELLED);
		job.setEndTime(getTime());
		ourSim.getTraceCollector().jobEnded(getTime(), job, broker.getId());
		
		SchedulerHelper.finishJob(job, broker, ourSim, getTime());
		SchedulerHelper.updateScheduler(ourSim, broker, getTime());
	}

	private void cancelTask(Broker broker, OurSim ourSim, Task task) {
		if (ExecutionState.RUNNING.equals(task.getState()) || 
				ExecutionState.UNSTARTED.equals(task.getState())) {
			task.setState(ExecutionState.CANCELLED);
			task.setEndTime(getTime());
			ourSim.getTraceCollector().taskEnded(getTime(), task, broker.getId());
		}
	}

	private void cancelReplica(Broker broker, OurSim ourSim, Replica replica) {
		if (ExecutionState.RUNNING.equals(replica.getState())) {
			replica.setState(ExecutionState.CANCELLED);
			replica.setEndTime(getTime());
			ourSim.getTraceCollector().replicaEnded(
					getTime(), replica, broker.getId());
			
			Job job = replica.getTask().getJob();
			SchedulerHelper.disposeWorker(job, job.getRequest().getSpec().getId(), broker,
					replica.getWorker(), ourSim, getTime());
			
			replica.setWorker(null);
		}
	}

}

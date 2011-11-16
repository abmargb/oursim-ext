package br.edu.ufcg.lsd.oursim.trace;

import java.io.IOException;
import java.io.OutputStream;

import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;

public class DefaultTraceCollector implements TraceCollector {

	private final OutputStream stream;

	public DefaultTraceCollector(OutputStream stream) {
		this.stream = stream;
	}
	
	@Override
	public void replicaEnded(long time, Replica replica, String brokerId) {
		String message = createTrace("REPLICA_ENDED", time,  
				replica.getState(), 
				replica.getEndTime() - replica.getCreationTime(),
				brokerId, 
				replica.getWorker(), 
				replica.getId(), 
				replica.getTask().getId(),
				replica.getTask().getJob().getId());
		
		writeMessage(message);
	}
	
	@Override
	public void jobEnded(long time, Job job, String brokerId) {
		String message = createTrace("JOB_ENDED", time,  
				job.getState(),
				job.getEndTime() - job.getCreationTime(),
				brokerId,
				job.getId());
		
		writeMessage(message);
	}

	@Override
	public void taskEnded(long time, Task task, String brokerId) {
		String message = createTrace("TASK_ENDED", time,  
				task.getState(), 
				task.getEndTime() - task.getCreationTime(),
				brokerId,
				task.getId(), task.getJob().getId());
		
		writeMessage(message);
	}

	private void writeMessage(String message) {
		try {
			stream.write(message.getBytes());
			stream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String createTrace(String traceType, long time, Object... parameters) {
		StringBuilder builder = new StringBuilder(traceType);
		builder.append(":").append(time);
		for (Object param : parameters) {
			builder.append(":").append(param);
		}
		builder.append("\n");
		return builder.toString();
	}

}

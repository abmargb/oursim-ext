package br.edu.ufcg.lsd.oursim.trace;

import java.io.ByteArrayOutputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;

public class DefaultTraceCollectorTest {

	private ByteArrayOutputStream baos;
	private DefaultTraceCollector collector;
	private Job job;
	private Task task;
	private Replica replica;

	@Before
	public void setup() {
		this.baos = new ByteArrayOutputStream();
		this.collector = new DefaultTraceCollector(baos);
		
		this.job = new Job();
		job.setCreationTime(100);
		job.setEndTime(150);
		job.setId(1);
		job.setState(ExecutionState.FINISHED);
		
		this.task = new Task();
		task.setCreationTime(100);
		task.setEndTime(150);
		task.setId(1);
		task.setState(ExecutionState.FINISHED);
		
		job.addTask(task);
		task.setJob(job);
		
		this.replica = new Replica();
		replica.setState(ExecutionState.FINISHED);
		replica.setCreationTime(100);
		replica.setEndTime(150);
		replica.setId(1);
		replica.setWorker("worker");
		
		task.addReplica(replica);
		replica.setTask(task);
	}
	
	@Test
	public void testReplicaEnded() {
		collector.replicaEnded(0, replica, "brokerId");
		
		byte[] byteArray = baos.toByteArray();
		String collected = new String(byteArray);
		String expected = "REPLICA_ENDED:0:FINISHED:50:brokerId:worker:1:1:1\n";
		
		Assert.assertEquals(expected, collected);
	}
	
	@Test
	public void testTaskEnded() {
		collector.taskEnded(0, task, "brokerId");
		
		byte[] byteArray = baos.toByteArray();
		String collected = new String(byteArray);
		
		String expected = "TASK_ENDED:0:FINISHED:50:brokerId:1:1\n";
		
		Assert.assertEquals(expected, collected);
	}
	
	@Test
	public void testJobEnded() {
		collector.jobEnded(0, job, "brokerId");
		
		byte[] byteArray = baos.toByteArray();
		String collected = new String(byteArray);
		
		String expected = "JOB_ENDED:0:FINISHED:50:brokerId:1\n";
		
		Assert.assertEquals(expected, collected);
	}
	
}

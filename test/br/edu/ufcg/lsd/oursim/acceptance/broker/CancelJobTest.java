package br.edu.ufcg.lsd.oursim.acceptance.broker;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

/**
 * Req 304
 *
 */
public class CancelJobTest extends AcceptanceTest {

	@Test
	public void testCancelJobNoBroker() {
		int jobId = 1;
		String brokerId = "broker1";
		addEvent(new EventSpec(BrokerEvents.CANCEL_JOB, 1, brokerId + " " + jobId));
	}

	@Test
	public void testCancelJobBrokerDown() {
		int jobId = 1;
		String brokerId = "broker1";
		createBroker(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.CANCEL_JOB, 1, brokerId + " " + jobId));
	}

	@Test
	public void testCancelJobNoJob() {
		int jobId = 1;
		String brokerId = "broker1";
		createBroker(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.CANCEL_JOB, 1, brokerId + " " + jobId));
	}
	
	@Test
	public void testCancelJobOneJob() {
		int jobId = 1;
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob));
		
		Assert.assertEquals(1, broker.getJobs().size());
		
		addEvent(new EventSpec(BrokerEvents.CANCEL_JOB, 2, brokerId + " " + jobId));
		
		Assert.assertEquals(ExecutionState.CANCELLED, 
				broker.getJob(jobId).getState());
		
	}
	
	@Test
	public void testCancelJobMoreThanOneJob() {
		int jobId1 = 1;
		int jobId2 = 2;
		String brokerId = "broker1";
		
		Broker broker = createBroker(brokerId);
		String jsonJob1 = "{id:1 , tasks:[{duration:10000}]}";
		String jsonJob2 = "{id:2 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob1));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 2, brokerId + " " + jsonJob2));
		
		Assert.assertEquals(2, broker.getJobs().size());
		
		addEvent(new EventSpec(BrokerEvents.CANCEL_JOB, 3, brokerId + " " + jobId2));
		
		Assert.assertEquals(ExecutionState.UNSTARTED, 
				broker.getJob(jobId1).getState());
		Assert.assertEquals(ExecutionState.CANCELLED, 
				broker.getJob(jobId2).getState());
		
	}
	
	@Test
	public void testCancelAllJobsMoreThanOneJob() {
		
		int jobId1 = 1;
		int jobId2 = 2;
		
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		String jsonJob1 = "{id:1 , tasks:[{duration:10000}]}";
		String jsonJob2 = "{id:2 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob1));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 2, brokerId + " " + jsonJob2));
		
		Assert.assertEquals(2, broker.getJobs().size());
		
		addEvent(new EventSpec(BrokerEvents.CANCEL_JOB, 3, brokerId + " " + jobId2));
		addEvent(new EventSpec(BrokerEvents.CANCEL_JOB, 4, brokerId + " " + jobId1));
		
		Assert.assertEquals(ExecutionState.CANCELLED, 
				broker.getJob(jobId1).getState());
		Assert.assertEquals(ExecutionState.CANCELLED, 
				broker.getJob(jobId2).getState());
		
	}
	
	@Test
	public void testCancelJobWithPeer() {
		int jobId = 1;
		String brokerId = "broker1";
		String peerId = "peer1";
		
		Broker broker = createBroker(brokerId);
		createPeer(peerId);
		broker.setPeerId(peerId);
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 2, brokerId + " " + jsonJob));
		List<Event> secondary = addEvent(new EventSpec(
				BrokerEvents.CANCEL_JOB, 3, brokerId + " " + jobId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary,
				PeerEvents.FINISH_REQUEST));
		Assert.assertEquals(ExecutionState.CANCELLED, 
				broker.getJob(jobId).getState());
		
	}
	
	@Test
	public void testCancelJobAfterHereIsWorker() {
		int jobId = 1;
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				BrokerEvents.HERE_IS_WORKER);
		
		List<Event> secondary = addEventAndReturn(new EventSpec(
				BrokerEvents.CANCEL_JOB, 4, brokerId + " " + jobId));
		
		Assert.assertTrue(EventRecorderUtils.hasEventSequence(secondary,
				PeerEvents.DISPOSE_WORKER, PeerEvents.FINISH_REQUEST));
		
		Assert.assertEquals(ExecutionState.CANCELLED, broker.getJob(jobId).getState());
		
	}
	
	@Test
	public void testCancelJobAfterWorkerAvailable() {
		int jobId = 1;
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				BrokerEvents.WORKER_AVAILABLE);
		
		List<Event> secondary = addEventAndReturn(new EventSpec(
				BrokerEvents.CANCEL_JOB, 4, brokerId + " " + jobId));
		
		Assert.assertTrue(EventRecorderUtils.hasEventSequence(secondary,
				PeerEvents.DISPOSE_WORKER, PeerEvents.FINISH_REQUEST));
		
		Assert.assertEquals(ExecutionState.CANCELLED, broker.getJob(jobId).getState());
	}
	
	@Test
	public void testCancelJobAfterWorkerScheduling() {
		int jobId = 1;
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				BrokerEvents.SCHEDULE);
		
		List<Event> secondary = addEventAndReturn(new EventSpec(
				BrokerEvents.CANCEL_JOB, 4, brokerId + " " + jobId));
		
		Assert.assertTrue(EventRecorderUtils.hasEventSequence(secondary,
				PeerEvents.DISPOSE_WORKER, PeerEvents.FINISH_REQUEST));
		
		Assert.assertEquals(ExecutionState.CANCELLED, broker.getJob(jobId).getState());
	}
	
	@Test
	public void testCancelJobAfterJobFinished() {
		int jobId = 1;
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		String jsonJob = "{id:1 , tasks:[{duration:0}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob));
		
		Assert.assertEquals(ExecutionState.FINISHED, broker.getJob(jobId).getState());
		
		addEvent(new EventSpec(
				BrokerEvents.CANCEL_JOB, 4, brokerId + " " + jobId));
		
		Assert.assertEquals(ExecutionState.CANCELLED, broker.getJob(jobId).getState());
	}
	
}


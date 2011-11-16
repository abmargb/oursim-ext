package br.edu.ufcg.lsd.oursim.acceptance.broker;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

/**
 * Req 330
 *
 */
public class WorkerFailedTest extends AcceptanceTest {

	@Test
	public void testWorkerFailedAfterHereIsWorker() {
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
		
		List<Event> secondary = addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 3, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, BrokerEvents.WORKER_FAILED));
		
	}
	
	@Test
	public void testWorkerFailedAfterRecovering() {
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
		
		List<Event> secondary = addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 3, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, BrokerEvents.WORKER_FAILED));
		
	}
	
	@Test
	public void testWorkerFailedAfterBeingScheduled() {
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
				WorkerEvents.START_WORK);
		
		Replica replica = broker.getJob(1).getTasks().get(0).getReplicas().get(0);
		
		List<Event> secondary = addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 3, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, BrokerEvents.WORKER_FAILED));
		Assert.assertEquals(ExecutionState.FAILED, replica.getState());
		Assert.assertEquals(ExecutionState.RUNNING, replica.getTask().getState());
		
	}
	
	@Test
	public void testWorkerFailedMaxFails() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		setProperty(Configuration.PROP_BROKER_MAX_FAILS, "1");
		
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
				WorkerEvents.START_WORK);
		
		Replica replica = broker.getJob(1).getTasks().get(0).getReplicas().get(0);
		
		List<Event> secondary = addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 3, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, BrokerEvents.WORKER_FAILED));
		Assert.assertEquals(ExecutionState.FAILED, replica.getState());
		Assert.assertEquals(ExecutionState.FAILED, replica.getTask().getState());
		
	}
	
	@Test
	public void testWorkerFailedAlreadyFailed() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		String workerId2 = "worker2";
		
		setProperty(Configuration.PROP_BROKER_MAX_FAILS, "1");
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		
		createWorker(workerId);
		peer.addWorker(workerId);
		createWorker(workerId2);
		peer.addWorker(workerId2);
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}, {duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId2));
		
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				WorkerEvents.START_WORK);
		
		Replica replicaTaskTwo = broker.getJob(1).getTasks().get(1).getReplicas().get(0);
		
		addEventAndReturn(new EventSpec(BrokerEvents.WORKER_FAILED, 4, brokerId, workerId));
		
		Assert.assertEquals(ExecutionState.FAILED, replicaTaskTwo.getState());
		Assert.assertEquals(ExecutionState.FAILED, replicaTaskTwo.getTask().getState());
		
		addEventAndReturn(new EventSpec(BrokerEvents.WORKER_FAILED, 5, brokerId, workerId2));
		
	}
	
	@Test
	public void testWorkerFailedAlreadyFailedTask() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		String workerId2 = "worker2";
		
		setProperty(Configuration.PROP_BROKER_MAX_REPLICAS, "2");
		setProperty(Configuration.PROP_BROKER_MAX_FAILS, "1");
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		
		createWorker(workerId);
		peer.addWorker(workerId);
		createWorker(workerId2);
		peer.addWorker(workerId2);
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId2));
		
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				WorkerEvents.START_WORK);
		
		Replica replica = broker.getJob(1).getTasks().get(0).getReplicas().get(1);
		
		addEventAndReturn(new EventSpec(BrokerEvents.WORKER_FAILED, 4, brokerId, workerId));
		
		Assert.assertEquals(ExecutionState.FAILED, replica.getState());
		Assert.assertEquals(ExecutionState.FAILED, replica.getTask().getState());
		
		addEventAndReturn(new EventSpec(BrokerEvents.WORKER_FAILED, 5, brokerId, workerId2));
		
	}
	
	@Test
	public void testWorkerFailedTwice() {
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
				WorkerEvents.START_WORK);
		
		Replica replica = broker.getJob(1).getTasks().get(0).getReplicas().get(0);
		
		List<Event> secondary = addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 3, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, BrokerEvents.WORKER_FAILED));
		Assert.assertEquals(ExecutionState.FAILED, replica.getState());
		Assert.assertEquals(ExecutionState.RUNNING, replica.getTask().getState());
		
		addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 4, workerId));
		
	}
	
	@Test
	public void testWorkerFailedAfterJobFinished() {
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
		
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob));
		addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 3, workerId));
		
	}
	
	@Test
	public void testWorkerFailedNeedMoreWorkers() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		setProperty(Configuration.PROP_BROKER_MAX_REPLICAS, "2");
		
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
				WorkerEvents.START_WORK);
		
		List<Event> secondary = addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 4, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, PeerEvents.RESUME_REQUEST));
		
	}
	
	@Test
	public void testWorkerFailedSatisfiedJob() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		String workerId2 = "worker2";
		String workerId3 = "worker3";
		String workerId4 = "worker4";
		
		setProperty(Configuration.PROP_BROKER_MAX_REPLICAS, "2");
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		
		createWorker(workerId);
		peer.addWorker(workerId);
		createWorker(workerId2);
		peer.addWorker(workerId2);
		createWorker(workerId3);
		peer.addWorker(workerId3);
		createWorker(workerId4);
		peer.addWorker(workerId4);
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}, {duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId2));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId3));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId4));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				WorkerEvents.START_WORK);
		
		List<Event> secondary = addEvent(new EventSpec(WorkerEvents.WORKER_DOWN, 4, workerId));
		
		Assert.assertFalse(EventRecorderUtils.hasEvent(secondary, PeerEvents.RESUME_REQUEST));
		
	}
}

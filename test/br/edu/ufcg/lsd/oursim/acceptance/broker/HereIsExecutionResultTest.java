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
 * Req 314
 *
 */
public class HereIsExecutionResultTest extends AcceptanceTest {

	@Test
	public void testHereIsExecutionResultAfterJobCancelled() {
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
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				WorkerEvents.START_WORK);
		
		Replica replica = broker.getJob(1).getTasks().get(0).getReplicas().get(0);
		
		addEvent(new EventSpec(
				BrokerEvents.CANCEL_JOB, 4, brokerId + " " + jobId));
		addEvent(new EventSpec(
				BrokerEvents.HERE_IS_EXECUTION_RESULT, 5, replica, brokerId));
		
		Assert.assertNull(replica.getWorker());
		
	}
	
	@Test
	public void testHereIsExecutionResultWithTwoTasks() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		String jsonJob = "{id:1 , tasks:[{duration:0}, {duration:0}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				WorkerEvents.START_WORK);
		
		Replica replica = broker.getJob(1).getTasks().get(0).getReplicas().get(0);

		List<Event> secondary = addEvent(new EventSpec(
				BrokerEvents.HERE_IS_EXECUTION_RESULT, 5, replica, brokerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, WorkerEvents.START_WORK));
		
	}
	
	@Test
	public void testHereIsExecutionResultWithReplication() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		String workerId2 = "worker12";
		
		setProperty(Configuration.PROP_BROKER_MAX_REPLICAS, "2");
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		
		createWorker(workerId);
		peer.addWorker(workerId);
		createWorker(workerId2);
		peer.addWorker(workerId2);
		
		String jsonJob = "{id:1 , tasks:[{duration:0}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId2));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob), 
				WorkerEvents.START_WORK);
		
		List<Replica> replicas = broker.getJob(1).getTasks().get(0).getReplicas();
		Replica replica = replicas.get(0);

		addEvent(new EventSpec(
				BrokerEvents.HERE_IS_EXECUTION_RESULT, 5, replica, brokerId));
		
		int aborted = 0;
		for (Replica siblingReplica : replicas) {
			if (siblingReplica.getState().equals(ExecutionState.ABORTED)) {
				aborted++;
			}
		}
		
		Assert.assertEquals(1, aborted);
		
	}

	
}

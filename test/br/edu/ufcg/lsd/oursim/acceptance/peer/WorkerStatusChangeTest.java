package br.edu.ufcg.lsd.oursim.acceptance.peer;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.WorkerState;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

/**
 * Req 025
 *
 */
public class WorkerStatusChangeTest extends AcceptanceTest {

	@Test
	public void workerChangeFromUnavailableToIdle() {
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_IDLE, 2, workerId, peerId));
		
		Assert.assertEquals(WorkerState.IDLE, peer.getWorkerState(workerId));
		Assert.assertNotNull(peer.getAllocation(workerId));
	}
	
	@Test
	public void workerChangeFromIdleToUnavailable() {
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_IDLE, 2, workerId, peerId));
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_FAILED, 3, peerId, workerId));
		
		Assert.assertEquals(WorkerState.UNAVAILABLE, peer.getWorkerState(workerId));
		Assert.assertNull(peer.getAllocation(workerId));
	}
	
	@Test
	public void workerChangeFromIdleToInUse() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		broker.setPeerId(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(brokerId);
		requestSpec.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId, requestSpec, false));
		
		Assert.assertEquals(WorkerState.IN_USE, peer.getWorkerState(workerId));
		Assert.assertNotNull(peer.getAllocation(workerId));
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_IN_USE, 4, requestSpec, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, BrokerEvents.HERE_IS_WORKER));
	}
	
	@Test
	public void workerChangeFromIdleToDonated() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String peerId2 = "peer2";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		createPeer(peerId2);
		broker.setPeerId(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId2));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(brokerId);
		requestSpec.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 3, peerId2, 
						peerId, requestSpec));
		
		Assert.assertEquals(WorkerState.IN_USE, peer.getWorkerState(workerId));
		Assert.assertFalse(peer.getAllocation(workerId).isConsumerLocal());
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_DONATED, 3, peerId2, 
						peerId, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, PeerEvents.REMOTE_HERE_IS_WORKER));
	}
}

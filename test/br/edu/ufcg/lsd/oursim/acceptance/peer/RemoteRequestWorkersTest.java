package br.edu.ufcg.lsd.oursim.acceptance.peer;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.WorkerState;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class RemoteRequestWorkersTest extends AcceptanceTest {

	@Test
	public void testRemoteRequestAllocation() {
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
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 3, peerId2, 
						peerId, requestSpec));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, 
				WorkerEvents.WORK_FOR_PEER));
		
		Assert.assertEquals(WorkerState.IN_USE, peer.getWorkerState(workerId));
		
		Allocation allocation = peer.getAllocation(workerId);
		Assert.assertEquals(peerId2, allocation.getConsumer());
		Assert.assertEquals(false, allocation.isConsumerLocal());
	}
	
	@Test
	public void testRemoteAllocationNoWorkers() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String peerId2 = "peer2";
		
		Broker broker = createBroker(brokerId);
		createPeer(peerId);
		createPeer(peerId2);
		broker.setPeerId(peerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId2));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(brokerId);
		requestSpec.setRequiredWorkers(1);
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 3, peerId2, 
						peerId, requestSpec));
		
		Assert.assertFalse(EventRecorderUtils.hasEvent(secondary, 
				WorkerEvents.WORK_FOR_PEER));
		
	}
	
	@Test
	public void testRemoteRequestNoNof() {
		
		String worker1Id = "worker1";
		String worker2Id = "worker2";
		String worker3Id = "worker3";
		
		String peerId = "peer1";
		String peerId2 = "peer2";
		String peerId3 = "peer3";
		String peerId4 = "peer4";
		String peerId5 = "peer5";
		
		Peer peer = createPeer(peerId);
		createPeer(peerId2);
		createPeer(peerId3);
		createPeer(peerId4);
		createPeer(peerId5);
		
		createWorker(worker1Id);
		peer.addWorker(worker1Id);
		createWorker(worker2Id);
		peer.addWorker(worker2Id);
		createWorker(worker3Id);
		peer.addWorker(worker3Id);
		
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker1Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker2Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker3Id));
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId2));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId3));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId4));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId5));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setRequiredWorkers(3);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 3, peerId2, peerId, requestSpec));
		
		Assert.assertEquals(peerId2, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker3Id).getConsumer());
		
		RequestSpec requestSpec2 = new RequestSpec();
		requestSpec2.setId(1L);
		requestSpec2.setRequiredWorkers(2);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 4, peerId3, peerId, requestSpec2));
		
		Assert.assertEquals(peerId3, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker3Id).getConsumer());
		
		RequestSpec requestSpec3 = new RequestSpec();
		requestSpec3.setId(2L);
		requestSpec3.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 5, peerId4, peerId, requestSpec3));
		
		Assert.assertEquals(peerId3, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(peerId4, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker3Id).getConsumer());
		
		RequestSpec requestSpec4 = new RequestSpec();
		requestSpec4.setId(3L);
		requestSpec4.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 6, peerId5, peerId, requestSpec4));
		
		Assert.assertEquals(peerId3, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(peerId4, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker3Id).getConsumer());
		
	}
	
}

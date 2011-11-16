package br.edu.ufcg.lsd.oursim.acceptance.peer;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class HereIsRemoteWorkerTest extends AcceptanceTest {

	@Test
	public void testHereIsRemoteWorker() {
		String peerId = "peer1";
		String peerId2 = "peer2";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		Peer peer2 = createPeer(peerId2);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId2));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 3, workerId));
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 4, peerId2, peerId, workerId));
		
		Assert.assertNotNull(peer2.getMonitor(workerId));
		Assert.assertFalse(peer2.getMonitor(workerId).isUp());
		
	}
	
	@Test
	public void testHereIsRemoteWorkerLocalRedistribution() {
		String broker1Id = "broker1";
		String broker2Id = "broker2";
		
		String worker1Id = "worker1";
		String worker2Id = "worker2";
		
		String peerId1 = "peer1";
		String peerId2 = "peer2";
		
		Peer peer1 = createPeer(peerId1);
		Peer peer2 = createPeer(peerId2);
		
		Broker broker1 = createBroker(broker1Id);
		broker1.setPeerId(peerId2);
		Broker broker2 = createBroker(broker2Id);
		broker2.setPeerId(peerId2);
		
		createWorker(worker1Id);
		peer1.addWorker(worker1Id);
		createWorker(worker2Id);
		peer1.addWorker(worker2Id);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId1));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId2));
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker1Id));
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker2Id));
		
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker1Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker2Id));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(broker1Id);
		requestSpec.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId2, requestSpec, false));
		
		RequestSpec requestSpec2 = new RequestSpec();
		requestSpec2.setId(1L);
		requestSpec2.setBrokerId(broker2Id);
		requestSpec2.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 4, peerId2, requestSpec2, false));
		
		//Receive worker1
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 5, peerId2, peerId1, worker1Id));
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_AVAILABLE, 6, peerId2, worker1Id));
		
		//Receive worker2
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 7, peerId2, peerId1, worker2Id));
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_AVAILABLE, 8, peerId2, worker2Id));
		
		Assert.assertEquals(broker2Id, peer2.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(broker1Id, peer2.getAllocation(worker1Id).getConsumer());
	}
	
	@Test
	public void testRemoteWorkerAvailableNoProvider() {
		String broker1Id = "broker1";
		
		String worker1Id = "worker1";
		
		String peerId1 = "peer1";
		String peerId2 = "peer2";
		
		Peer peer1 = createPeer(peerId1);
		Peer peer2 = createPeer(peerId2);
		
		Broker broker1 = createBroker(broker1Id);
		broker1.setPeerId(peerId2);
		
		createWorker(worker1Id);
		peer1.addWorker(worker1Id);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId1));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId2));
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker1Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker1Id));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(broker1Id);
		requestSpec.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId2, requestSpec, false));
		
		//Receive worker1
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_AVAILABLE, 4, peerId2, worker1Id));
		
		Assert.assertNull(peer2.getAllocation(worker1Id));
	}
	
}

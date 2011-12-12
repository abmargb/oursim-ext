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
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.WorkerState;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class RequestWorkersTest extends AcceptanceTest {

	@Test
	public void testRequestLocalAllocation() {
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
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId, requestSpec, false));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, 
				WorkerEvents.WORK_FOR_BROKER));
		Assert.assertFalse(EventRecorderUtils.hasEvent(secondary, 
				PeerEvents.REQUEST_WORKERS));
		Assert.assertFalse(EventRecorderUtils.hasEvent(secondary, 
				PeerEvents.REMOTE_REQUEST_WORKERS));
		
		Assert.assertEquals(WorkerState.IN_USE, peer.getWorkerState(workerId));
		Assert.assertEquals(brokerId, peer.getAllocation(workerId).getConsumer());
		
	}
	
	@Test
	public void testRequestRepetition() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		Broker broker = createBroker(brokerId);
		createPeer(peerId);
		broker.setPeerId(peerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(brokerId);
		requestSpec.setRequiredWorkers(1);
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId, requestSpec, false));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, 
				PeerEvents.REQUEST_WORKERS));
		Assert.assertFalse(EventRecorderUtils.hasEvent(secondary, 
				PeerEvents.REMOTE_REQUEST_WORKERS));
		
	}
	
	@Test
	public void testRequestForwardToCommunity() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String peerId2 = "peer2";
		String workerId = "workerId"; 
		String dsId = "dsId";
		
		Peer peer = createPeer(peerId);
		peer.setDiscoveryServiceId(dsId);
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		Peer peer2 = createPeer(peerId2);
		createWorker(workerId);
		peer2.addWorker(workerId);
		peer2.setDiscoveryServiceId(dsId);
		
		createDiscoveryService(dsId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId2));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(DiscoveryServiceEvents.DS_UP, 2, dsId));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(brokerId);
		requestSpec.setRequiredWorkers(1);
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId, requestSpec, false));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, 
				PeerEvents.REQUEST_WORKERS));
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, 
				PeerEvents.REMOTE_REQUEST_WORKERS));
		
	}
	
	@Test
	public void testRequestLocalReallocation() {
		String broker1Id = "broker1";
		String broker2Id = "broker2";
		String broker3Id = "broker3";
		String broker4Id = "broker4";
		
		String worker1Id = "worker1";
		String worker2Id = "worker2";
		String worker3Id = "worker3";
		
		String peerId = "peer1";
		
		Peer peer = createPeer(peerId);
		
		Broker broker1 = createBroker(broker1Id);
		broker1.setPeerId(peerId);
		Broker broker2 = createBroker(broker2Id);
		broker2.setPeerId(peerId);
		Broker broker3 = createBroker(broker3Id);
		broker3.setPeerId(peerId);
		Broker broker4 = createBroker(broker4Id);
		broker4.setPeerId(peerId);
		
		createWorker(worker1Id);
		peer.addWorker(worker1Id);
		createWorker(worker2Id);
		peer.addWorker(worker2Id);
		createWorker(worker3Id);
		peer.addWorker(worker3Id);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker1Id));
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker2Id));
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker3Id));
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker4Id));
		
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker1Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker2Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker3Id));
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(broker1Id);
		requestSpec.setRequiredWorkers(3);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId, requestSpec, false));
		
		Assert.assertEquals(broker1Id, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(broker1Id, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(broker1Id, peer.getAllocation(worker3Id).getConsumer());
		
		RequestSpec requestSpec2 = new RequestSpec();
		requestSpec2.setId(1L);
		requestSpec2.setBrokerId(broker2Id);
		requestSpec2.setRequiredWorkers(2);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 4, peerId, requestSpec2, false));
		
		Assert.assertEquals(broker2Id, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(broker1Id, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(broker1Id, peer.getAllocation(worker3Id).getConsumer());
		
		RequestSpec requestSpec3 = new RequestSpec();
		requestSpec3.setId(2L);
		requestSpec3.setBrokerId(broker3Id);
		requestSpec3.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 5, peerId, requestSpec3, false));
		
		Assert.assertEquals(broker2Id, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(broker3Id, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(broker1Id, peer.getAllocation(worker3Id).getConsumer());
		
		RequestSpec requestSpec4 = new RequestSpec();
		requestSpec4.setId(3L);
		requestSpec4.setBrokerId(broker4Id);
		requestSpec4.setRequiredWorkers(1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 6, peerId, requestSpec4, false));
		
		Assert.assertEquals(broker2Id, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(broker3Id, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(broker1Id, peer.getAllocation(worker3Id).getConsumer());
		
	}
	
	@Test
	public void testLocalAndRemoteReallocation() {
		String broker1Id = "broker1";
		
		String worker1Id = "worker1";
		String worker2Id = "worker2";
		String worker3Id = "worker3";
		
		String peerId = "peer1";
		String peerId2 = "peer2";
		
		Peer peer = createPeer(peerId);
		createPeer(peerId2);
		
		Broker broker1 = createBroker(broker1Id);
		broker1.setPeerId(peerId);
		
		createWorker(worker1Id);
		peer.addWorker(worker1Id);
		createWorker(worker2Id);
		peer.addWorker(worker2Id);
		createWorker(worker3Id);
		peer.addWorker(worker3Id);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker1Id));
		
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker1Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker2Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker3Id));
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setRequiredWorkers(2);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 3, peerId2, peerId, requestSpec));
		
		Assert.assertEquals(peerId2, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(null, peer.getAllocation(worker3Id).getConsumer());
		
		RequestSpec requestSpec2 = new RequestSpec();
		requestSpec2.setId(1L);
		requestSpec2.setBrokerId(broker1Id);
		requestSpec2.setRequiredWorkers(2);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 4, peerId, requestSpec2, false));
		
		Assert.assertEquals(broker1Id, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(broker1Id, peer.getAllocation(worker3Id).getConsumer());
		
	}
	
	@Test
	public void testLocalAndRemoteRedistributionConsideringNof() {
		
		//Create two user accounts
		String broker1Id = "broker1";
		String broker2Id = "broker2";
		
		String peerId = "peer1";
		String peerId2 = "peer2";
		
		String worker1Id = "worker1";
		String worker2Id = "worker2";
		String worker3Id = "worker3";
		
		Peer peer = createPeer(peerId);
		createPeer(peerId2);
		
		Broker broker1 = createBroker(broker1Id);
		broker1.setPeerId(peerId);
		
		createWorker(worker1Id);
		peer.addWorker(worker1Id);
		createWorker(worker2Id);
		peer.addWorker(worker2Id);
		createWorker(worker3Id);
		peer.addWorker(worker3Id);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker1Id));
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, broker2Id));
		
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker1Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker2Id));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, worker3Id));
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setRequiredWorkers(2);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 3, peerId2, peerId, requestSpec));
		
		Assert.assertEquals(peerId2, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(null, peer.getAllocation(worker3Id).getConsumer());
		
		RequestSpec requestSpec2 = new RequestSpec();
		requestSpec2.setId(1L);
		requestSpec2.setBrokerId(broker1Id);
		requestSpec2.setRequiredWorkers(2);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 4, peerId, requestSpec2, false));
		
		Assert.assertEquals(broker1Id, peer.getAllocation(worker1Id).getConsumer());
		Assert.assertEquals(peerId2, peer.getAllocation(worker2Id).getConsumer());
		Assert.assertEquals(broker1Id, peer.getAllocation(worker3Id).getConsumer());
		
	}
}

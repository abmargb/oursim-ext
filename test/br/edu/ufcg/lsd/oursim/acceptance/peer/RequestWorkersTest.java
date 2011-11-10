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
	
}

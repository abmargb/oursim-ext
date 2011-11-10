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
	
}

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
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class RemoteWorkerFailedTest extends AcceptanceTest {

	@Test
	public void testRemoteWorkerFailedBeforeDelivered() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String peerId2 = "peer2";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId2);
	
		Peer peer = createPeer(peerId);
		createPeer(peerId2);
		
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
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId2, requestSpec, false));
		addEventAndReturn(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 4, peerId2, peerId, workerId));
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_FAILED, 5, peerId2, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, PeerEvents.DISPOSE_REMOTE_WORKER));
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, FailureDetectionEvents.RELEASE));
		
	}
	
	@Test
	public void testRemoteWorkerFailedAfterDelivered() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String peerId2 = "peer2";
		String workerId = "worker1";
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId2);
	
		Peer peer = createPeer(peerId);
		createPeer(peerId2);
		
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
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId2, requestSpec, false));
		addEvent(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 4, peerId2, peerId, workerId),
				WorkerEvents.WORK_FOR_BROKER);
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_FAILED, 5, peerId2, workerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, PeerEvents.DISPOSE_REMOTE_WORKER));
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, FailureDetectionEvents.RELEASE));
		
	}
	
}

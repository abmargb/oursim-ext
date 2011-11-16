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
import br.edu.ufcg.lsd.oursim.events.peer.WorkerState;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class LocalWorkerFailedTest extends AcceptanceTest {

	@Test
	public void testLocalWorkerFailure() {
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_FAILED, 4, peerId, workerId));
		
		Assert.assertNotNull(peer.getMonitor(workerId));
		
	}
	
	@Test
	public void testLocalWorkerFailureNotRecovered() {
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_FAILED, 4, peerId, workerId));
		
		Assert.assertNotNull(peer.getMonitor(workerId));
		
	}
	
	@Test
	public void testLocalWorkerFailureDonatedWorker() {
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
				new EventSpec(PeerEvents.REMOTE_REQUEST_WORKERS, 3, peerId2, 
						peerId, requestSpec));
		
		addEventAndReturn(
				new EventSpec(PeerEvents.WORKER_FAILED, 4, peerId, workerId));
		
		Assert.assertEquals(WorkerState.UNAVAILABLE, peer.getWorkerState(workerId));
	}
	
}

package br.edu.ufcg.lsd.oursim.acceptance.broker;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class SetGridTest extends AcceptanceTest {

	@Test
	public void testBrokerStartAndSetGrid() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		Peer peer = createPeer(peerId);
		Broker broker = createBroker(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		List<Event> secondary = addEvent(new EventSpec(BrokerEvents.SET_GRID, 2, brokerId + " " + peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEventSequence(secondary,
				BrokerEvents.PEER_AVAILABLE, PeerEvents.BROKER_LOGIN, BrokerEvents.BROKER_LOGGED));
		Assert.assertNotNull(broker.getMonitor(peerId));
		Assert.assertTrue(peer.getBrokersIds().contains(brokerId));
		Assert.assertTrue(broker.getMonitor(peerId).isUp());
	}
	
	@Test
	public void testSetGridWithSatisfiedJob() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		Broker broker = createBroker(brokerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(BrokerEvents.SET_GRID, 3, brokerId + " " + peerId));
		
		String jsonJob1 = "{id:1 , tasks:[{duration:1000}]}";
		
		addEvent(new EventSpec(
				BrokerEvents.ADD_JOB, 4, brokerId + " " + jsonJob1));
		
		String peerId2 = "peer2";
		createPeer(peerId2);
		addEvent(new EventSpec(PeerEvents.PEER_UP, 5, peerId2));
		addEvent(new EventSpec(BrokerEvents.SET_GRID, 6, brokerId + " " + peerId2));
		
		Assert.assertNull(broker.getMonitor(peerId));
		Assert.assertFalse(peer.getBrokersIds().contains(brokerId));
	}
	
	@Test
	public void testSetGridToSamePeer() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		Broker broker = createBroker(brokerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(BrokerEvents.SET_GRID, 3, brokerId + " " + peerId));
		
		String jsonJob1 = "{id:1 , tasks:[{duration:1000}]}";
		
		addEvent(new EventSpec(
				BrokerEvents.ADD_JOB, 4, brokerId + " " + jsonJob1));
		
		addEvent(new EventSpec(BrokerEvents.SET_GRID, 5, brokerId + " " + peerId));
		
		Assert.assertNotNull(broker.getMonitor(peerId));
		Assert.assertTrue(peer.getBrokersIds().contains(brokerId));
	}
	
}

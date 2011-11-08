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

/**
 * Req 328, 327
 *
 */
public class PeerFailedTest extends AcceptanceTest {

	@Test
	public void testPeerFailedWithNoJobs() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		createPeer(peerId);
		createBroker(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(BrokerEvents.SET_GRID, 2, brokerId + " " + peerId));
		List<Event> secondary = addEvent(new EventSpec(PeerEvents.PEER_DOWN, 3, peerId));
	
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, BrokerEvents.PEER_FAILED));
	}
	
	@Test
	public void testPeerFailedWithSatisfiedJob() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		createBroker(brokerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(BrokerEvents.SET_GRID, 3, brokerId + " " + peerId));
		
		String jsonJob1 = "{id:1 , tasks:[{duration:1000}]}";
		
		addEvent(new EventSpec(
				BrokerEvents.ADD_JOB, 4, brokerId + " " + jsonJob1));
		
		List<Event> secondary = addEvent(new EventSpec(PeerEvents.PEER_DOWN, 5, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, BrokerEvents.PEER_FAILED));
	}
	
	@Test
	public void testPeerFailedWithUnstartedJob() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		createPeer(peerId);
		Broker broker = createBroker(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(BrokerEvents.SET_GRID, 3, brokerId + " " + peerId));
		
		String jsonJob1 = "{id:1 , tasks:[{duration:1000}]}";
		
		addEvent(new EventSpec(
				BrokerEvents.ADD_JOB, 4, brokerId + " " + jsonJob1));
		
		List<Event> secondary = addEvent(new EventSpec(PeerEvents.PEER_DOWN, 5, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, BrokerEvents.PEER_FAILED));
		Assert.assertTrue(broker.getRequests().isEmpty());
	}
	
	@Test
	public void testPeerFailedThenPeerRecovery() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		createPeer(peerId);
		Broker broker = createBroker(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(BrokerEvents.SET_GRID, 3, brokerId + " " + peerId));
		
		String jsonJob1 = "{id:1 , tasks:[{duration:1000}]}";
		
		addEvent(new EventSpec(
				BrokerEvents.ADD_JOB, 4, brokerId + " " + jsonJob1));
		
		List<Event> secondary = addEvent(new EventSpec(PeerEvents.PEER_DOWN, 5, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, BrokerEvents.PEER_FAILED));
		Assert.assertTrue(broker.getRequests().isEmpty());
		
		secondary = addEvent(new EventSpec(PeerEvents.PEER_UP, 6, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, PeerEvents.REQUEST_WORKERS));
	}
	
}

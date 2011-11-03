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

/**
 * Req 301, 302
 * 
 */
public class StartBrokerTest extends AcceptanceTest {

	@Test
	public void testBrokerStartNoPeer() {
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		
		Assert.assertFalse(broker.isUp());
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		
		Assert.assertTrue(broker.isUp());
	}
	
	@Test
	public void testBrokerStartAndAddJobNoPeer() {
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob));
		
		Assert.assertEquals(1, broker.getJobs().size());
	}
	
	@Test
	public void testBrokerStartWithPeerDown() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		createPeer(peerId);
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		
		Assert.assertNotNull(broker.getMonitor(peerId));
		Assert.assertFalse(broker.getMonitor(peerId).isUp());
	}
	
	@Test
	public void testBrokerStartAndAddJobPeerDown() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		createPeer(peerId);
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob));
		
		Assert.assertEquals(1, broker.getJobs().size());
	}
	
	@Test
	public void testBrokerStartWithPeerUp() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		Peer peer = createPeer(peerId);
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		List<Event> secondary = addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEventSequence(secondary,
				BrokerEvents.PEER_AVAILABLE, PeerEvents.BROKER_LOGIN, BrokerEvents.BROKER_LOGGED));
		Assert.assertNotNull(broker.getMonitor(peerId));
		Assert.assertTrue(peer.getBrokersIds().contains(brokerId));
		Assert.assertTrue(broker.getMonitor(peerId).isUp());
	}
	
	@Test
	public void testBrokerStartAnAlreadyStartedOne() {
		String brokerId = "broker1";
		
		Broker broker = createBroker(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		Assert.assertTrue(broker.isUp());
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 1, brokerId));
		Assert.assertTrue(broker.isUp());
	}
	
}

package br.edu.ufcg.lsd.oursim.acceptance.broker;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

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
	public void testBrokerStartWithPeerUp() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		createPeer(peerId);
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		
		Assert.assertNotNull(broker.getMonitor(peerId));
		Assert.assertTrue(broker.getMonitor(peerId).isUp());
	}
	
}

package br.edu.ufcg.lsd.oursim.acceptance.peer;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class BrokerAvailableTest extends AcceptanceTest {

	@Test
	public void testBrokerAvailable() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		createBroker(brokerId);
		Peer peer = createPeer(peerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.BROKER_AVAILABLE, 2, peerId, brokerId));
		
		Assert.assertTrue(peer.getBrokersIds().isEmpty());
	}
	
}

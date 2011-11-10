package br.edu.ufcg.lsd.oursim.acceptance.peer;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class StopPeerTest extends AcceptanceTest {

	@Test
	public void testStopPeer() {
		String peerId = "peer1";
		Peer peer = createPeer(peerId);
		
		Assert.assertFalse(peer.isUp());
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId));
		
		Assert.assertTrue(peer.isUp());
		
		addEvent(new EventSpec(PeerEvents.PEER_DOWN, 1, peerId));
		
		Assert.assertFalse(peer.isUp());
	}
	
}

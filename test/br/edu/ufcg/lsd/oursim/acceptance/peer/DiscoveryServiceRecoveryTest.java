package br.edu.ufcg.lsd.oursim.acceptance.peer;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class DiscoveryServiceRecoveryTest extends AcceptanceTest {

	@Test
	public void testDiscoveryServiceRecovery() {
		String peerId = "peer1";
		String dsId = "dsId";
		
		Peer peer = createPeer(peerId);
		peer.setDiscoveryServiceId(dsId);
		createDiscoveryService(dsId);
		
		addEvent(new EventSpec(DiscoveryServiceEvents.DS_UP, 0, dsId));
		List<Event> secondary = addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, 
				PeerEvents.REPEAT_GET_WORKER_PROVIDERS));
		
	}
	
	@Test
	public void testDiscoveryServiceRecoveryWithPeers() {
		String peerId = "peer1";
		String peerId2 = "peer2";
		String dsId = "dsId";
		
		Peer peer1 = createPeer(peerId);
		peer1.setDiscoveryServiceId(dsId);
		
		Peer peer2 = createPeer(peerId2);
		peer2.setDiscoveryServiceId(dsId);
		
		createDiscoveryService(dsId);
		
		addEvent(new EventSpec(DiscoveryServiceEvents.DS_UP, 0, dsId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId2));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		
		Assert.assertTrue(peer1.getWorkerProviders().contains(peerId2));
		
	}
	
}

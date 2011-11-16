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

public class DiscoveryServiceFailedTest extends AcceptanceTest {

	@Test
	public void testDiscoveryServiceFailed() {
		String peerId = "peer1";
		String dsId = "dsId";
		
		Peer peer = createPeer(peerId);
		peer.setDiscoveryServiceId(dsId);
		createDiscoveryService(dsId);
		
		addEvent(new EventSpec(DiscoveryServiceEvents.DS_UP, 0, dsId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(DiscoveryServiceEvents.DS_DOWN, 1, dsId));
		
		List<Event> secondary = addEventAndReturn(new EventSpec(
				PeerEvents.REPEAT_GET_WORKER_PROVIDERS, 2, peerId, dsId));
		
		Assert.assertFalse(EventRecorderUtils.hasEvent(secondary, 
				PeerEvents.REPEAT_GET_WORKER_PROVIDERS));
	}
	
	
}

package br.edu.ufcg.lsd.oursim.acceptance.fd;

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
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.fd.FixedPingFailureDetector;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class LivenessCheckTest extends AcceptanceTest {

	@Test
	public void testLivenessCheckNotFailed() {

		setProperty(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.TRUE.toString());
		
		String brokerId = "broker1";
		String peerId = "peer1";
		
		createPeer(peerId);
		
		Broker broker = createBroker(brokerId);
		broker.setFailureDetector(new FixedPingFailureDetector());
		broker.setPeerId(peerId);

		addEventAndReturn(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(FailureDetectionEvents.LIVENESS_CHECK, 0, brokerId, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, FailureDetectionEvents.LIVENESS_CHECK));
	}
	
	@Test
	public void testLivenessCheckFailed() {

		setProperty(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.TRUE.toString());
		
		String brokerId = "broker1";
		String peerId = "peer1";
		
		Peer peer = createPeer(peerId);
		peer.setFailureDetector(new FixedPingFailureDetector());
		
		Broker broker = createBroker(brokerId);
		broker.setFailureDetector(new FixedPingFailureDetector());
		broker.setPeerId(peerId);

		addEventAndReturn(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEventAndReturn(new EventSpec(PeerEvents.PEER_UP, 0, peerId));
		
		addEventAndReturn(new EventSpec(FailureDetectionEvents.UPDATE_STATUS_AVAILABLE, 0, brokerId, peerId));
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(FailureDetectionEvents.LIVENESS_CHECK, 10, brokerId, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(secondary, BrokerEvents.PEER_FAILED));
	}
	
}

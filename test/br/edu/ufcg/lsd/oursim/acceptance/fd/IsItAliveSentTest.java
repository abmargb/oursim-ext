package br.edu.ufcg.lsd.oursim.acceptance.fd;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;
import br.edu.ufcg.lsd.oursim.fd.FixedPingFailureDetector;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class IsItAliveSentTest extends AcceptanceTest {

	@Test
	public void testIsItAliveSentNoDetector() {
		
		String brokerId = "broker1";
		String peerId = "peer1";
		Broker broker = createBroker(brokerId);
		createPeer(peerId);
		broker.setPeerId(peerId);

		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(FailureDetectionEvents.IS_IT_ALIVE_SENT, 0, brokerId, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, FailureDetectionEvents.IS_IT_ALIVE_RECEIVED));
		Assert.assertFalse(EventRecorderUtils.hasEvent(
				secondary, FailureDetectionEvents.IS_IT_ALIVE_SENT));
	}
	
	@Test
	public void testIsItAliveSentWithDetector() {
		
		setProperty(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.TRUE.toString());
		
		String brokerId = "broker1";
		String peerId = "peer1";
		
		createPeer(peerId);
		
		Broker broker = createBroker(brokerId);
		broker.setFailureDetector(new FixedPingFailureDetector());
		broker.setPeerId(peerId);

		addEventAndReturn(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(FailureDetectionEvents.IS_IT_ALIVE_SENT, 0, brokerId, peerId));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, FailureDetectionEvents.IS_IT_ALIVE_RECEIVED));
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, FailureDetectionEvents.IS_IT_ALIVE_SENT));
	}
	
}

package br.edu.ufcg.lsd.oursim.acceptance.broker;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;

/**
 * Req 303
 *
 */
public class StopBrokerTest extends AcceptanceTest {

	@Test
	public void testBrokerStopNoPeer() {
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		
		Assert.assertFalse(broker.isUp());
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		
		Assert.assertTrue(broker.isUp());
		
		addEvent(new EventSpec(BrokerEvents.BROKER_DOWN, 1, brokerId));
		
		Assert.assertFalse(broker.isUp());
	}
	
	@Test
	public void testBrokerStopTwice() {
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		
		Assert.assertFalse(broker.isUp());
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		
		Assert.assertTrue(broker.isUp());
		
		addEvent(new EventSpec(BrokerEvents.BROKER_DOWN, 1, brokerId));
		
		Assert.assertFalse(broker.isUp());
		
		addEvent(new EventSpec(BrokerEvents.BROKER_DOWN, 2, brokerId));
		
		Assert.assertFalse(broker.isUp());
	}
	
}

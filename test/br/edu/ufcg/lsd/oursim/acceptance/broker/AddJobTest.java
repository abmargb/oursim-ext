package br.edu.ufcg.lsd.oursim.acceptance.broker;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

/**
 * Req 304
 *
 */
public class AddJobTest extends AcceptanceTest {

	@Test
	public void testAddJobNotStartedBroker() {
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob));
		
		Assert.assertTrue(broker.getJobs().isEmpty());
	}
	
	@Test
	public void testAddJobStartedBroker() {
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob));
		
		Assert.assertFalse(broker.getJobs().isEmpty());
	}
	
	@Test
	public void testAddMoreThanOneJob() {
		String brokerId = "broker1";
		Broker broker = createBroker(brokerId);
		String jsonJob1 = "{id:1 , tasks:[{duration:10000}]}";
		String jsonJob2 = "{id:2 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob1));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 2, brokerId + " " + jsonJob2));
		
		Assert.assertEquals(2, broker.getJobs().size());
	}
	
	@Test
	public void testAddJobWithPeerDown() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		Broker broker = createBroker(brokerId);
		createPeer(peerId);
		broker.setPeerId(peerId);
		
		String jsonJob1 = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(BrokerEvents.ADD_JOB, 1, brokerId + " " + jsonJob1));
		
		Assert.assertEquals(1, broker.getJobs().size());
		Assert.assertTrue(broker.getRequests().isEmpty());
	}
	
	@Test
	public void testAddJobWithPeerUp() {
		String brokerId = "broker1";
		String peerId = "peer1";
		
		Broker broker = createBroker(brokerId);
		createPeer(peerId);
		broker.setPeerId(peerId);
		
		String jsonJob1 = "{id:1 , tasks:[{duration:10000}]}";
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEventAndHalt(new EventSpec(BrokerEvents.ADD_JOB, 2, brokerId + " " + jsonJob1));
		
		Assert.assertEquals(1, broker.getJobs().size());
		Assert.assertEquals(1, broker.getRequests().size());
		
	}
	
}

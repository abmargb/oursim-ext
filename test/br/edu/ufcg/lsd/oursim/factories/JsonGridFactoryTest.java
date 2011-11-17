package br.edu.ufcg.lsd.oursim.factories;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.DiscoveryService;
import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class JsonGridFactoryTest {

	@Test
	public void testParseGrid() {
		JsonGridFactory factory = new JsonGridFactory(
				Configuration.createDefaults(), 
				getClass().getResourceAsStream("grid-example.conf"));
		
		Grid grid = factory.createGrid();
		
		Assert.assertEquals(DiscoveryService.class, grid.getObject("ds").getClass());
		Assert.assertEquals(Worker.class, grid.getObject("worker1A").getClass());
		Assert.assertEquals(Broker.class, grid.getObject("broker1A").getClass());
		Assert.assertEquals(Peer.class, grid.getObject("peerA").getClass());
		
		Peer peerA = grid.getObject("peerA");
		Peer peerB = grid.getObject("peerB");
		
		Assert.assertTrue(peerA.getWorkersIds().contains("worker1A"));
		Assert.assertTrue(peerB.getWorkersIds().contains("worker1B"));
		
		Broker broker1A = grid.getObject("broker1A");
		
		Assert.assertEquals("peerA", broker1A.getPeerId());
		Assert.assertEquals("ds", peerA.getDiscoveryServiceId());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadGrid() {
		JsonGridFactory factory = new JsonGridFactory(
				Configuration.createDefaults(), 
				getClass().getResourceAsStream("grid-bad.conf"));
		
		factory.createGrid();
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadPeer() {
		JsonGridFactory factory = new JsonGridFactory(
				Configuration.createDefaults(), 
				getClass().getResourceAsStream("grid-badpeer.conf"));
		
		factory.createGrid();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testBadWorker() {
		JsonGridFactory factory = new JsonGridFactory(
				Configuration.createDefaults(), 
				getClass().getResourceAsStream("grid-badworker.conf"));
		
		factory.createGrid();
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadBroker() {
		JsonGridFactory factory = new JsonGridFactory(
				Configuration.createDefaults(), 
				getClass().getResourceAsStream("grid-badbroker.conf"));
		
		factory.createGrid();
		
	}
	
	@Test
	public void testNoDetector() {
		Properties properties = Configuration.createDefaults();
		properties.setProperty(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.FALSE.toString());
		
		JsonGridFactory factory = new JsonGridFactory(
				properties, 
				getClass().getResourceAsStream("grid-example.conf"));
		
		factory.createGrid();
	}
	
	@Test
	public void testFailureDetectorOptParser() {
		Properties properties = Configuration.createDefaults();
		properties.setProperty(Configuration.PROP_FAILURE_DETECTOR_NAME, "chen");
		properties.setProperty("FD_chen_alpha", "15000");
		
		JsonGridFactory factory = new JsonGridFactory(
				properties, 
				getClass().getResourceAsStream("grid-example.conf"));
		
		factory.createGrid();
	}
}

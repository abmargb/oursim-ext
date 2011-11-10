package br.edu.ufcg.lsd.oursim.acceptance.peer;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

/**
 * Req 010
 * 
 */
public class StartPeerTest extends AcceptanceTest {

	@Test
	public void testStartPeer() {
		String peerId = "peer1";
		Peer peer = createPeer(peerId);
		
		Assert.assertFalse(peer.isUp());
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId));
		
		Assert.assertTrue(peer.isUp());
	}
	
	@Test
	public void testStartPeerWithWorkersDown() {
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId));
	
		Assert.assertNotNull(peer.getMonitor(workerId));
		Assert.assertFalse(peer.getMonitor(workerId).isUp());
	}
	
	@Test
	public void testStartPeerWithWorkersUp() {
		String peerId = "peer1";
		String workerId = "worker1";
		
		Peer peer = createPeer(peerId);
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, workerId));
	
		Assert.assertNotNull(peer.getMonitor(workerId));
		Assert.assertTrue(peer.getMonitor(workerId).isUp());
	}
	
	@Test
	public void testStartPeerWithDiscoveryServiceDown() {
		String peerId = "peer1";
		String dsId = "ds1";
		
		Peer peer = createPeer(peerId);
		createDiscoveryService(dsId);
		peer.setDiscoveryServiceId(dsId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId));
	
		Assert.assertNotNull(peer.getMonitor(dsId));
		Assert.assertFalse(peer.getMonitor(dsId).isUp());
	}
	
	@Test
	public void testStartPeerWithDiscoveryServiceUp() {
		String peerId = "peer1";
		String dsId = "ds1";
		
		Peer peer = createPeer(peerId);
		createDiscoveryService(dsId);
		peer.setDiscoveryServiceId(dsId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 0, peerId));
		addEvent(new EventSpec(DiscoveryServiceEvents.DS_UP, 0, dsId));
	
		Assert.assertNotNull(peer.getMonitor(dsId));
		Assert.assertTrue(peer.getMonitor(dsId).isUp());
	}
}

package br.edu.ufcg.lsd.oursim.acceptance.broker;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.RequestWorkersEvent;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

/**
 * Req 312
 *
 */
public class HereIsWorkerTest extends AcceptanceTest {

	@Test
	public void testHereIsWorkerWithNoPeer() {
		String brokerId = "broker1";
		String workerId = "worker1";
		createBroker(brokerId);
		createWorker(workerId);
		
		RequestSpec spec = new RequestSpec();
		spec.setId(1);
		spec.setBrokerId(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, workerId));
		addEvent(new EventSpec(BrokerEvents.HERE_IS_WORKER, 2, workerId, spec));
	}
	
	@Test
	public void testHereIsWorkerWithPeerDown() {
		String brokerId = "broker1";
		String workerId = "worker1";
		String peerId = "peer1";
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(createPeer(peerId).getId());
		createWorker(workerId);
		
		RequestSpec spec = new RequestSpec();
		spec.setId(1);
		spec.setBrokerId(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, workerId));
		addEvent(new EventSpec(BrokerEvents.HERE_IS_WORKER, 2, workerId, spec));
	}
	
	@Test
	public void testHereIsWorkerWithNoJobs() {
		String brokerId = "broker1";
		String workerId = "worker1";
		String peerId = "peer1";
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(createPeer(peerId).getId());
		createWorker(workerId);
		
		RequestSpec spec = new RequestSpec();
		spec.setId(1);
		spec.setBrokerId(brokerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, workerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId));
		List<Event> secondary = addEvent(new EventSpec(BrokerEvents.HERE_IS_WORKER, 
				3, workerId, spec));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, PeerEvents.DISPOSE_WORKER));
	}
	
	@Test
	public void testHereIsWorkerWithJobs() {
		String brokerId = "broker1";
		String workerId = "worker1";
		String peerId = "peer1";
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(createPeer(peerId).getId());
		createWorker(workerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, workerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId));
		
		List<Event> secondary = addEvent(new EventSpec(
				BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob));
		RequestWorkersEvent requestWorkersEvents = EventRecorderUtils.getEvent(
				secondary, PeerEvents.REQUEST_WORKERS);
		
		secondary = addEvent(new EventSpec(BrokerEvents.HERE_IS_WORKER, 
				4, workerId, requestWorkersEvents.getRequestSpec()));
		
		Assert.assertTrue(EventRecorderUtils.hasEventSequence(
				secondary, BrokerEvents.WORKER_AVAILABLE, 
				BrokerEvents.SCHEDULE, WorkerEvents.START_WORK));
	}
	
	@Test
	public void testHereIsWorkerWithFinishedJob() {
		String brokerId = "broker1";
		String workerId = "worker1";
		String peerId = "peer1";
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		
		broker.setPeerId(peer.getId());
		createWorker(workerId);
		peer.addWorker(workerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, workerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId));
		
		List<Event> secondary = addEvent(new EventSpec(
				BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob));
		RequestWorkersEvent requestWorkersEvents = EventRecorderUtils.getEvent(
				secondary, PeerEvents.REQUEST_WORKERS);
		
		secondary = addEvent(new EventSpec(BrokerEvents.HERE_IS_WORKER, 
				4, workerId, requestWorkersEvents.getRequestSpec()));
		
		Assert.assertTrue(EventRecorderUtils.hasEvent(
				secondary, PeerEvents.DISPOSE_WORKER));
	}
	
	
	
}

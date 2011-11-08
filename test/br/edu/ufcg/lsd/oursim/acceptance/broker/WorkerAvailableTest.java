package br.edu.ufcg.lsd.oursim.acceptance.broker;

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
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.RequestWorkersEvent;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class WorkerAvailableTest extends AcceptanceTest {

	@Test
	public void testWorkerIsAvailableWithCancelledJob() {
		String brokerId = "broker1";
		String workerId = "worker1";
		String peerId = "peer1";
		int jobId = 1;
		
		String jsonJob = "{id:1 , tasks:[{duration:10000}]}";
		
		Broker broker = createBroker(brokerId);
		Peer peer = createPeer(peerId);
		
		broker.setPeerId(peer.getId());
		createWorker(workerId);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 1, workerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 2, peerId));
		
		List<Event> secondary = addEvent(new EventSpec(
				BrokerEvents.ADD_JOB, 3, brokerId + " " + jsonJob));
		RequestWorkersEvent requestWorkersEvents = EventRecorderUtils.getEvent(
				secondary, PeerEvents.REQUEST_WORKERS);
		
		addEvent(new EventSpec(BrokerEvents.HERE_IS_WORKER, 
				4, workerId, requestWorkersEvents.getRequestSpec()));
		
		addEvent(new EventSpec(BrokerEvents.CANCEL_JOB, 5, brokerId + " " + jobId));
		secondary = addEvent(new EventSpec(BrokerEvents.WORKER_AVAILABLE, 
				6, brokerId, workerId));
		
		Assert.assertTrue(secondary.isEmpty());
		
	}
	
}

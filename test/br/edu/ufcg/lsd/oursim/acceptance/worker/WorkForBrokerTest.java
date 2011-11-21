package br.edu.ufcg.lsd.oursim.acceptance.worker;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.acceptance.EventRecorderUtils;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class WorkForBrokerTest extends AcceptanceTest {

	@Test
	public void testWorkForBrokerWithRemotePeer() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String remotePeerId1 = "peer2";
		String remoteWorkerId1 = "worker2";
		
		createPeer(peerId);
		Peer remPeer1 = createPeer(remotePeerId1);
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		Worker worker = createWorker(remoteWorkerId1);
		worker.setCpu(1.);
		remPeer1.addWorker(remoteWorkerId1);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, remotePeerId1));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, remoteWorkerId1));
		
		addEventAndReturn(
				new EventSpec(WorkerEvents.WORK_FOR_PEER, 3, peerId, remotePeerId1, remoteWorkerId1));
		List<Event> secondary = addEventAndReturn(
				new EventSpec(WorkerEvents.WORK_FOR_BROKER, 3, brokerId, new RequestSpec(), remoteWorkerId1));
		
		Assert.assertTrue(
				EventRecorderUtils.hasEvent(secondary, PeerEvents.WORKER_IN_USE));
		
	}
	
	@Test
	public void testWorkForBrokerTwiceWithRemotePeer() {
		String brokerId = "broker1";
		String brokerId2 = "broker2";
		String peerId = "peer1";
		String remotePeerId1 = "peer2";
		String remoteWorkerId1 = "worker2";
		
		createPeer(peerId);
		Peer remPeer1 = createPeer(remotePeerId1);
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		Broker broker2 = createBroker(brokerId2);
		broker2.setPeerId(remotePeerId1);
		
		Worker worker = createWorker(remoteWorkerId1);
		worker.setCpu(1.);
		remPeer1.addWorker(remoteWorkerId1);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, remotePeerId1));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, remoteWorkerId1));
		
		addEventAndReturn(
				new EventSpec(WorkerEvents.WORK_FOR_PEER, 3, peerId, remotePeerId1, remoteWorkerId1));
		addEventAndReturn(
				new EventSpec(WorkerEvents.WORK_FOR_BROKER, 3, brokerId, new RequestSpec(), remoteWorkerId1));
		
		Task task = new Task();
		task.setDuration(10);
		Replica replica = new Replica();
		replica.setTask(task);
		
		addEventAndReturn(
				new EventSpec(WorkerEvents.START_WORK, 3, replica, remoteWorkerId1, brokerId));
		
		List<Event> secondary = addEventAndReturn(
				new EventSpec(WorkerEvents.WORK_FOR_BROKER, 3, brokerId2, new RequestSpec(), remoteWorkerId1));
		
		Assert.assertTrue(
				EventRecorderUtils.hasEvent(secondary, PeerEvents.WORKER_IN_USE));
		
	}
	
}

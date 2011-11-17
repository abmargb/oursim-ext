package br.edu.ufcg.lsd.oursim.acceptance.peer;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.accounting.ReplicaAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class ReportReplicaAccountingTest extends AcceptanceTest {

	
	@Test
	public void testEmptyNof() {
		String peerId = "peer1";
		Peer peer = createPeer(peerId);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		
		Assert.assertTrue(peer.getBalances().isEmpty());
	}
	
	@Test
	public void testNoF() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String remotePeerId1 = "peer2";
		String remotePeerId2 = "peer3";
		String workerId = "worker1";
		String remoteWorkerId1 = "worker2";
		String remoteWorkerId2 = "worker3";
		
		Peer peer = createPeer(peerId);
		Peer remPeer1 = createPeer(remotePeerId1);
		Peer remPeer2 = createPeer(remotePeerId2);
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		createWorker(workerId);
		peer.addWorker(workerId);
		createWorker(remoteWorkerId1);
		remPeer1.addWorker(remoteWorkerId1);
		createWorker(remoteWorkerId2);
		remPeer2.addWorker(remoteWorkerId2);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, remotePeerId1));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, remotePeerId2));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, remoteWorkerId1));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, remoteWorkerId2));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(brokerId);
		requestSpec.setRequiredWorkers(5);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId, requestSpec, false));
		addEvent(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 3, peerId, remotePeerId1, remoteWorkerId1),
				PeerEvents.WORKER_AVAILABLE);
		addEvent(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 3, peerId, remotePeerId2, remoteWorkerId2),
				PeerEvents.WORKER_AVAILABLE);
		
		
		ReplicaAccounting replicaAcc = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId1, brokerId, 
				5, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 4, replicaAcc, peerId));
		
		ReplicaAccounting replicaAcc2 = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId1, brokerId, 
				7, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 5, replicaAcc2, peerId));
		
		ReplicaAccounting replicaAcc3 = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId2, brokerId, 
				7, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 6, replicaAcc3, peerId));
		
		ReplicaAccounting replicaAcc4 = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId2, brokerId, 
				9, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 7, replicaAcc4, peerId));
		
		ReplicaAccounting replicaAcc5 = new ReplicaAccounting(
				requestSpec.getId(), workerId, brokerId, 
				3, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 8, replicaAcc5, peerId));
		
		// Expect the Network of favours status to be empty
		Assert.assertTrue(peer.getBalances().isEmpty());
		
		// Finish the request
		addEventAndReturn(
				new EventSpec(PeerEvents.FINISH_REQUEST, 9, peerId, requestSpec));
		
		Map<String, Double> balances = peer.getBalances();
		
		Assert.assertEquals(6., balances.get(remotePeerId1));
		Assert.assertEquals(6., balances.get(remotePeerId2));
	}
	
	
	@Test
	public void testNoFOnlyRemote() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String remotePeerId1 = "peer2";
		String remotePeerId2 = "peer3";
		String remoteWorkerId1 = "worker2";
		String remoteWorkerId2 = "worker3";
		
		Peer peer = createPeer(peerId);
		Peer remPeer1 = createPeer(remotePeerId1);
		Peer remPeer2 = createPeer(remotePeerId2);
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		createWorker(remoteWorkerId1);
		remPeer1.addWorker(remoteWorkerId1);
		createWorker(remoteWorkerId2);
		remPeer2.addWorker(remoteWorkerId2);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, remotePeerId1));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, remotePeerId2));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, remoteWorkerId1));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, remoteWorkerId2));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(brokerId);
		requestSpec.setRequiredWorkers(5);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId, requestSpec, false));
		addEvent(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 3, peerId, remotePeerId1, remoteWorkerId1),
				PeerEvents.WORKER_AVAILABLE);
		addEvent(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 3, peerId, remotePeerId2, remoteWorkerId2),
				PeerEvents.WORKER_AVAILABLE);
		
		
		ReplicaAccounting replicaAcc = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId1, brokerId, 
				5, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 4, replicaAcc, peerId));
		
		ReplicaAccounting replicaAcc2 = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId1, brokerId, 
				7, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 5, replicaAcc2, peerId));
		
		ReplicaAccounting replicaAcc3 = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId2, brokerId, 
				7, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 6, replicaAcc3, peerId));
		
		ReplicaAccounting replicaAcc4 = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId2, brokerId, 
				9, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 7, replicaAcc4, peerId));
		
		// Expect the Network of favours status to be empty
		Assert.assertTrue(peer.getBalances().isEmpty());
		
		// Finish the request
		addEventAndReturn(
				new EventSpec(PeerEvents.FINISH_REQUEST, 9, peerId, requestSpec));
		
		Map<String, Double> balances = peer.getBalances();
		
		Assert.assertEquals(14., balances.get(remotePeerId1));
		Assert.assertEquals(14., balances.get(remotePeerId2));
	}
}

package br.edu.ufcg.lsd.oursim.acceptance.peer;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.acceptance.AcceptanceTest;
import br.edu.ufcg.lsd.oursim.entities.accounting.ReplicaAccounting;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class ReportWorkAccountingTest extends AcceptanceTest {

	
	@Test
	public void testNonNegativeNof() {
		String peerId = "peer1";
		String peer2Id = "peer2";
		Peer peer = createPeer(peerId);
		
		WorkAccounting workAccounting = new WorkAccounting("worker", peer2Id);
		workAccounting.setCPUTime(10);
		
		List<WorkAccounting> accountings = new LinkedList<WorkAccounting>();
		accountings.add(workAccounting);
		
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_WORK_ACCOUNTING, 1, peerId, accountings));
		
		Assert.assertEquals(0., peer.getBalances().get(peer2Id));
	}
	
	@Test
	public void testNoF() {
		String brokerId = "broker1";
		String peerId = "peer1";
		String remotePeerId1 = "peer2";
		String workerId = "worker1";
		String remoteWorkerId1 = "worker2";
		
		Peer peer = createPeer(peerId);
		Peer remPeer1 = createPeer(remotePeerId1);
		
		Broker broker = createBroker(brokerId);
		broker.setPeerId(peerId);
		
		createWorker(workerId);
		peer.addWorker(workerId);
		createWorker(remoteWorkerId1);
		remPeer1.addWorker(remoteWorkerId1);
		
		addEvent(new EventSpec(BrokerEvents.BROKER_UP, 0, brokerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, peerId));
		addEvent(new EventSpec(PeerEvents.PEER_UP, 1, remotePeerId1));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, workerId));
		addEvent(new EventSpec(WorkerEvents.WORKER_UP, 2, remoteWorkerId1));
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setId(0L);
		requestSpec.setBrokerId(brokerId);
		requestSpec.setRequiredWorkers(5);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REQUEST_WORKERS, 3, peerId, requestSpec, false));
		addEvent(
				new EventSpec(PeerEvents.REMOTE_HERE_IS_WORKER, 3, peerId, remotePeerId1, remoteWorkerId1),
				PeerEvents.WORKER_AVAILABLE);
		
		// Report accounting
		ReplicaAccounting replicaAcc = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId1, brokerId, 
				5, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 4, replicaAcc, peerId));
		
		ReplicaAccounting replicaAcc2 = new ReplicaAccounting(
				requestSpec.getId(), remoteWorkerId1, brokerId, 
				7, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 5, replicaAcc2, peerId));
		
		ReplicaAccounting replicaAcc3 = new ReplicaAccounting(
				requestSpec.getId(), workerId, brokerId, 
				3, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 6, replicaAcc3, peerId));
		
		ReplicaAccounting replicaAcc4 = new ReplicaAccounting(
				requestSpec.getId(), workerId, brokerId, 
				5, ExecutionState.FINISHED);
		
		addEventAndReturn(new EventSpec(PeerEvents.REPORT_REPLICA_ACCOUNTING, 7, replicaAcc4, peerId));
		
		// Expect the Network of favours status to be empty
		Assert.assertTrue(peer.getBalances().isEmpty());
		
		// Finish the request
		addEventAndReturn(
				new EventSpec(PeerEvents.FINISH_REQUEST, 9, peerId, requestSpec));
		
		
		Assert.assertEquals(8., peer.getBalances().get(remotePeerId1));
		
		// Report a donated favour
		List<WorkAccounting> accountings = new LinkedList<WorkAccounting>();
		
		WorkAccounting accounting1 = new WorkAccounting(remoteWorkerId1, remotePeerId1);
		accounting1.setCPUTime(4);
		accountings.add(accounting1);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REPORT_WORK_ACCOUNTING, 10, peerId, accountings));
		
		Assert.assertEquals(4., peer.getBalances().get(remotePeerId1));
		
		// Report a donated favour
		accountings.clear();
		WorkAccounting accounting2 = new WorkAccounting(remoteWorkerId1, remotePeerId1);
		accounting2.setCPUTime(5);
		accountings.add(accounting2);
		
		addEventAndReturn(
				new EventSpec(PeerEvents.REPORT_WORK_ACCOUNTING, 11, peerId, accountings));
		
		Assert.assertEquals(0., peer.getBalances().get(remotePeerId1));
	}
	
}

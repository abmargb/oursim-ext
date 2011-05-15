package br.edu.ufcg.lsd.oursim.events.peer.accounting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.accounting.ReplicaAccounting;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;

public class AccountingHelper {

	public static void commitReplicaAccountings(Peer peer, long requestId) {
		
		Map<String, List<ReplicaAccounting>> processes = getAccountingsByProvider(
				peer.getReplicaAccountings(requestId));

		AccountingEvaluator evaluator = new AccountingEvaluator(processes, peer.getId());
		evaluator.evaluate();

		for (String providerId : processes.keySet()) {
			if (!peer.getId().equals(providerId)) {
				
				double balance = getOldBalance(peer, peer.getId(), providerId)
						+ evaluator.getCPU(providerId);
				peer.setBalance(providerId, balance);
			}
		}

		peer.removeReplicaAccountings(requestId);
	}

	private static double getOldBalance(Peer peer, String localPeer, String remotePeer) {
		double oldBalance = 0;
		Map<String, Double> myBalances = peer.getBalances();
		if (myBalances != null) {
			Double providerBalances = myBalances.get(remotePeer);
			if (providerBalances != null) {
				oldBalance = providerBalances;
			}
		}
		
		return oldBalance;
	}

	private static Map<String, List<ReplicaAccounting>> getAccountingsByProvider(
			List<ReplicaAccounting> replicaAccountings) {
		
		Map<String, List<ReplicaAccounting>> accountingsByProvider = 
			new HashMap<String, List<ReplicaAccounting>>();
		
		for (ReplicaAccounting replicaAccounting : replicaAccountings) {
			List<ReplicaAccounting> providerAccountings = accountingsByProvider.get(
					replicaAccounting.getProvider());
			
			if (providerAccountings == null) {
				providerAccountings = new LinkedList<ReplicaAccounting>();
				accountingsByProvider.put(replicaAccounting.getProvider(), 
						providerAccountings);
			}
			providerAccountings.add(replicaAccounting);
		}
		
		return accountingsByProvider;
	}

	public static void commitWorkerAccounting(Peer peer,
			WorkAccounting workAccounting) {
		
		String consumerPeer = workAccounting.getRemotePeerId();
		double oldBalance = getOldBalance(peer, 
				peer.getId(), consumerPeer);
		double newBalance = Math.max(oldBalance - workAccounting.getCPUTime(), 0);
		
		peer.setBalance(consumerPeer, newBalance);
	}
	
}

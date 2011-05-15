/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package br.edu.ufcg.lsd.oursim.events.peer.accounting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.accounting.ReplicaAccounting;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;

/**
 *
 */
public class RelativeCPUAccountingStrategy implements AccountingStrategy {

	private final String localPeerId;
	
	public RelativeCPUAccountingStrategy(String localPeerId) {
		this.localPeerId = localPeerId;
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.refactoring.peer.controller.accounting.AccountingStrategy#evaluate(java.util.Map)
	 */
	public Map<String, Double> evaluate(
			Map<String, List<ReplicaAccounting>> processes) {
		
		Map<String, Double> cpuBalances = new HashMap<String, Double>();
		
		double totalCpuTime = 0.;
		int receivedFavoursCount = 0;
		
		for (String providerDN : processes.keySet()) {
			double totalCpuPerPeer = 0.;
			
			double finishedReplicaCpuPerPeer = 0.;
			int finishedReplicasCount = 0;
			
			for (ReplicaAccounting process : processes.get(providerDN)) {
				Double cpuTime = (double) process.getCpuTime();
				totalCpuPerPeer += cpuTime;
				
				if (process.getState().equals(ExecutionState.FINISHED)) {
					finishedReplicaCpuPerPeer += cpuTime;
					finishedReplicasCount++;
				}
				
			}
			
			if (providerDN.equals(localPeerId)) {
				totalCpuTime += finishedReplicaCpuPerPeer;
				receivedFavoursCount += finishedReplicasCount;
			} else {
				double averageCpuPerPeer = totalCpuPerPeer/(finishedReplicaCpuPerPeer/finishedReplicasCount);
				cpuBalances.put(providerDN, averageCpuPerPeer);
			}
			
		}
		
		double totalAverage = totalCpuTime / receivedFavoursCount;
		
		for (String providerServiceID : cpuBalances.keySet()) {
			cpuBalances.put(providerServiceID, cpuBalances.get(providerServiceID) * totalAverage);
		}

		return cpuBalances;
	}

}

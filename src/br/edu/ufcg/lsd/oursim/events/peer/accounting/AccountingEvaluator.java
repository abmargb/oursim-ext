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

import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.accounting.ReplicaAccounting;

/**
 * Receives associations of providers and <code> ReplicaAccounting </code>
 * and calculates the attributes' balances for each provider.
 */
public class AccountingEvaluator {

	private final Map<String, List<ReplicaAccounting>> accountings;
	private final String localPeerId;
	private Map<String, Double> cpuBalances;
	
	/**
	 * @param accountings
	 */
	public AccountingEvaluator(Map<String, List<ReplicaAccounting>> accountings, 
			String localPeerId) {
		this.accountings = accountings;
		this.localPeerId = localPeerId;
	}
	
	/**
	 * Evaluates the data and cpu balances according to replica accountings
	 */
	public void evaluate() {
		AccountingStrategy cpuAccountingStrategy = null;
		
		if (isThereAnyLocalAccounting()) {
			cpuAccountingStrategy = new RelativeCPUAccountingStrategy(localPeerId);
		} else {
			cpuAccountingStrategy = new RemoteCPUAccountingStrategy();
		}
		
		cpuBalances = cpuAccountingStrategy.evaluate(accountings);
	}
	
	/**
	 * @param providerId
	 * @return The cpu balance of the provider
	 */
	public Double getCPU(String providerId) {
		return cpuBalances.get(providerId);
	}
	
	/**
	 * @return
	 */
	private boolean isThereAnyLocalAccounting() {
		
		for (String providerDN : accountings.keySet()) {
			if (localPeerId.equals(providerDN)) {
				return true;
			}
		}
		
		return false;
	}
}

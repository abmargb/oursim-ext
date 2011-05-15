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
 * Classes that implements this interface represent an algorithm (strategy)
 * to evaluate a determined attribute of the network of favors, 
 * based on replica accoutings
 */
public interface AccountingStrategy {

	/**
	 * Evaluate a determined attribute os a Network os favors
	 * @param accountings
	 * @return A map that associates a provider with the attribute balance
	 * evaluated by this strategy.
	 */
	public Map<String, Double> evaluate(Map<String, List<ReplicaAccounting>> accountings);
	
}

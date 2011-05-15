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
package br.edu.ufcg.lsd.oursim.entities.allocation;

import java.util.LinkedList;
import java.util.List;


public class AllocationInfo implements Comparable<AllocationInfo>{
		
	private final int deservedWorkers;
	private final String consumer;
	private final List<Allocation> temporaryAllocations;
	
	public AllocationInfo(int deservedWorkers, String consumer, 
			List<Allocation> consumerAllocations) {
		this.deservedWorkers = deservedWorkers;
		this.consumer = consumer;
		this.temporaryAllocations = new LinkedList<Allocation>();
		if (consumerAllocations != null) {
			temporaryAllocations.addAll(consumerAllocations);
		}
	}
	
	public void removeAllocation(Allocation allocableW) {
		temporaryAllocations.remove(allocableW);
	}

	public void addAllocation(Allocation allocableW) {
		temporaryAllocations.add(allocableW);
	}
	
	public List<Allocation> getAllocations(){
		return this.temporaryAllocations;
	}

	public int getBalance() {
		return (temporaryAllocations.size() - deservedWorkers);
	}
	
	public boolean isOverBalanced(){
		return getBalance() > 0;
	}
	
	public boolean isInBalanced(){
		return getBalance() < 0;
	}

	/**
	 * @return the deservedWorkers
	 */
	public double getDeservedWorkers() {
		return deservedWorkers;
	}
	
	/** 
	 * Sorted from the major balance to the minor
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AllocationInfo o) {
		
		if(o != null) {
			return (o.getBalance() - getBalance());
		}

		throw new NullPointerException();
	}

	/**
	 * @return
	 */
	public String getConsumer() {
		return consumer;
	}
	
}
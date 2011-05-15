package br.edu.ufcg.lsd.oursim.events.peer.allocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.allocation.AllocationInfo;

public class LowerPriorityAllocationHelper {

	public static void processAllocation(String consumerId,
			int allAllocations, int allocationsLeft,
			List<Allocation> allocationInRange,
			List<Allocation> workersToAllocate, Map<String, Double> balances) {

		List<Allocation> possibleWorkersToAllocate = 
			getAllocationsPriorityRange(consumerId, allocationInRange, allAllocations, workersToAllocate, balances);

        AllocationHelper.takeNeededWorkers(possibleWorkersToAllocate, allocationsLeft, workersToAllocate);
	}
	
	private static List<Allocation> getAllocationsPriorityRange(String consumer,
			List<Allocation> workersInRange, int totalAllocations, 
			List<Allocation> workersToAllocate, Map<String, Double> balances) {

		List<Allocation> possibleWorkersToAllocate = new LinkedList<Allocation>();

		//Generate the allocation info for the Allocations of a range. 
		List<AllocationInfo> othersConsumersInfo = 
			AllocationHelper.generateAllocationInfoList(consumer, workersInRange, totalAllocations );

		/* Removing the request AllocationInfo, this should not be in the taking algorithm */
		AllocationInfo requestInfo = AllocationHelper.getRequestInfo(othersConsumersInfo, consumer);
		for (Allocation worker : workersToAllocate) {
			requestInfo.addAllocation(worker);
		}
		othersConsumersInfo.remove(requestInfo);

		Allocation alloc = null;

		do {
			alloc = takeLeastNOFBalanced(consumer, othersConsumersInfo, balances);

			if(alloc != null) {
				workersInRange.remove(alloc);
				requestInfo.addAllocation(alloc);
				possibleWorkersToAllocate.add(alloc);
			}

		} while(alloc != null);

		return possibleWorkersToAllocate;
	}

	private static Allocation takeLeastNOFBalanced(
			String consumer, List<AllocationInfo> allocations, Map<String, Double> balances) {
		
		if (allocations.isEmpty()) {
			return null;
		}
		//if exists an idle worker, picks the newer 
		if (AllocationHelper.isThereAnIdleAllocation(allocations)) {
			return AllocationHelper.takeNewerAllocation(allocations.iterator().next());
		}
		
		//picks minimal prejudice removal
		AllocationInfo allocationToTake = AllocationHelper.getLeastNOFBalanced(allocations, 
				allocations, null, balances);
		Double leastPrejudice = AllocationHelper.getPrejudice(allocationToTake, null, 
				allocations, balances);
		
		double currentPrejudice = Double.MAX_VALUE;
		
		if (leastPrejudice > currentPrejudice || 
				(leastPrejudice == currentPrejudice && 
						AllocationHelper.getPeerBalance(consumer, balances) < 
						AllocationHelper.getPeerBalance(allocationToTake.getConsumer(), balances))) {
			return null;
		}

		//picks newer allocable
		return AllocationHelper.takeNewerAllocation(allocationToTake);
	}

}

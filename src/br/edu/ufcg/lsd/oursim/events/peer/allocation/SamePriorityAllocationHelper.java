package br.edu.ufcg.lsd.oursim.events.peer.allocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.allocation.AllocationInfo;

public class SamePriorityAllocationHelper {

	public static void processAllocation(String consumerId,
			int allAllocations, int allocationsLeft,
			List<Allocation> allocationInRange,
			List<Allocation> workersToAllocate, Map<String, Double> balances) {

		if (allocationInRange == null) {
			return;
		}
		
		List<Allocation> possibleWorkersToAllocate = 
			getPossibleWorkersToAllocate(consumerId, allocationInRange,
					allocationInRange.size() + workersToAllocate.size(), workersToAllocate, balances);
		
		AllocationHelper.takeNeededWorkers(possibleWorkersToAllocate, allocationsLeft, workersToAllocate);
		
	}

	private static List<Allocation> getPossibleWorkersToAllocate(
			String consumer, List<Allocation> allocables, int totalAllocableWorkers,
			List<Allocation> workersToAllocate, Map<String, Double> balances) {
		
		List<AllocationInfo> othersConsumersInfo = AllocationHelper.generateAllocationInfoList(consumer, allocables,
				totalAllocableWorkers);
		
		/* Removing the request AllocationInfo, this should not be in the taking algorithm */
		AllocationInfo requestInfo = AllocationHelper.getRequestInfo(othersConsumersInfo, consumer);
		for (Allocation worker : workersToAllocate) {
			requestInfo.addAllocation(worker);
		}
		othersConsumersInfo.remove(requestInfo);

		
		List<Allocation> result = new LinkedList<Allocation>();
		Allocation alloc = null;
		
		do {
			alloc = takeLeastNOFBalanced(consumer, othersConsumersInfo, requestInfo, balances);
			if(alloc == null) {
				break;
			}
			requestInfo.addAllocation(alloc);
			result.add(alloc);
		
		} while (alloc != null);
		
		return result;
	}

	private static Allocation takeLeastNOFBalanced(String consumer,
			List<AllocationInfo> infos,
			AllocationInfo requestInfo, Map<String, Double> balances) {
		
		//picks allocations that match requirements
		List<AllocationInfo> matchedAllocations = new LinkedList<AllocationInfo>(infos);
		if (matchedAllocations.isEmpty()) {
			return null;
		}
		
		//adds request info
		List<AllocationInfo> allAllocationsWithWinner = new LinkedList<AllocationInfo>(infos);
		if (requestInfo != null) {
			allAllocationsWithWinner.add(requestInfo);
		}
		
		//picks minimal prejudice removal
		AllocationInfo allocationToTake = AllocationHelper.getLeastNOFBalanced(matchedAllocations, 
				allAllocationsWithWinner, requestInfo, balances);;
		if (isEquallyBalanced(requestInfo, allocationToTake, balances)) {
			return null;
		}
		
		Double leastPrejudice = AllocationHelper.getPrejudice(allocationToTake, requestInfo, allAllocationsWithWinner, balances);
		
		double currentPrejudice = AllocationHelper.getPrejudice(null, null, allAllocationsWithWinner, balances);
		
		if (leastPrejudice > currentPrejudice || 
				(leastPrejudice == currentPrejudice && 
						AllocationHelper.getPeerBalance(consumer, balances) < 
						AllocationHelper.getPeerBalance(allocationToTake.getConsumer(), balances))) {
			return null;
		}
		
		//picks newer allocable
		return AllocationHelper.takeNewerAllocation(allocationToTake);
	}
	
	private static boolean isEquallyBalanced(AllocationInfo allocationA, AllocationInfo allocationB, Map<String, Double> balances) {
		return AllocationHelper.getPeerBalance(allocationA.getConsumer(), balances).equals(
				AllocationHelper.getPeerBalance(allocationB.getConsumer(), balances)) && 
			(allocationA.getBalance() >= 0 || allocationB.getBalance() < 0);
	}

}

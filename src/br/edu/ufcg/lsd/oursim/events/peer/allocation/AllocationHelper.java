package br.edu.ufcg.lsd.oursim.events.peer.allocation;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.allocation.AllocationInfo;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;

public class AllocationHelper {

	private enum Priority {IDLE, REMOTE, LOCAL}
	
	public static List<Allocation> getAllocationsForLocalRequest(Peer peer,
			PeerRequest request) {
		
		return getRangeBasedPriorityAllocation(request.getSpec().getBrokerId(), 
				request.getNeededWorkers(), peer.getAllocations(), 
				peer.getBalances(), Priority.LOCAL);
	}
	
	public static List<Allocation> getAllocationsForRemoteRequest(Peer peer,
			RequestSpec requestSpec, String consumer) {
		
		return getRangeBasedPriorityAllocation(consumer, 
				requestSpec.getRequiredWorkers(), peer.getAllocations(), 
				peer.getBalances(), Priority.REMOTE);
	}

	private static List<Allocation> getRangeBasedPriorityAllocation(
			String consumerId, int neededWorkers, List<Allocation> allocations,
			Map<String, Double> balances, Priority priority) {
		
		Map<Priority, List<Allocation>> allocationsByPriority = getAllocationsByPriority(allocations);
		List<Allocation> workersToAllocate = new LinkedList<Allocation>();
		
		for (Priority eachPriority : Priority.values()) {
			int allocationsLeft = neededWorkers - workersToAllocate.size();
			
			if (allocationsLeft <= 0) {
				break;
			}
			
			List<Allocation> allocationInPriority = allocationsByPriority.get(eachPriority);
			
			if (priority.equals(eachPriority)) {
				SamePriorityAllocationHelper.processAllocation(consumerId, allocations.size(), 
						allocationsLeft, allocationInPriority, workersToAllocate, balances);
				break;
			} else {
				LowerPriorityAllocationHelper.processAllocation(consumerId, allocations.size(), 
						allocationsLeft, allocationInPriority, workersToAllocate, balances);
			}
		}
		
		return workersToAllocate;
	}

	private static Map<Priority, List<Allocation>> getAllocationsByPriority(
			List<Allocation> allocations) {
		
		Map<Priority, List<Allocation>> allAllocations = new HashMap<AllocationHelper.Priority, List<Allocation>>();
		for (Priority eachPriority : Priority.values()) {
			allAllocations.put(eachPriority, new LinkedList<Allocation>());
		}
		
		for (Allocation allocation : allocations) {
			if (allocation.getConsumer() == null) {
				allAllocations.get(Priority.IDLE).add(allocation);
			} else if (allocation.isConsumerLocal()) {
				allAllocations.get(Priority.LOCAL).add(allocation);
			} else {
				allAllocations.get(Priority.REMOTE).add(allocation);
			}
		}
		
		return allAllocations;
	}

	public static List<AllocationInfo> generateAllocationInfoList(
			String consumer, List<Allocation> allocations,
			int totalWorkers) {
		
		Map<String, List<Allocation>> consumer2Allocations = createConsumersMap(allocations);

		/* Add a map entry without allocations, for the new consumer, if necessary */
		if(! consumer2Allocations.containsKey(consumer)) {
			consumer2Allocations.put(consumer, new LinkedList<Allocation>());
		}
		
		return generateAllocationInfo(allocations, totalWorkers, consumer2Allocations);
	}

	private static List<AllocationInfo> generateAllocationInfo(
			List<Allocation> allocations, int totalWorkers,
			Map<String, List<Allocation>> consumer2Allocations) {
		
		int totalConsumers = consumer2Allocations.keySet().size();
		List<AllocationInfo> info = new LinkedList<AllocationInfo>();
		
		for (Map.Entry<String, List<Allocation>> allocationEntry : consumer2Allocations.entrySet()) {
			
			int deservedWorkers = totalWorkers/totalConsumers;
			info.add(new AllocationInfo(deservedWorkers, allocationEntry.getKey(), 
					allocationEntry.getValue()));
		}
		
		List<Allocation> noConsumerAllocables = new LinkedList<Allocation>();
		/* Create AllocationInfo for the Allocables that have no consumer */
		for (Allocation allocableWorker : allocations) {
			if(allocableWorker.getConsumer() ==  null) {
				noConsumerAllocables.add(allocableWorker);
			}
		}
		
		if(!noConsumerAllocables.isEmpty()) {
			AllocationInfo noConsumerAllocations = new AllocationInfo(0, null, null);
			
			for (Allocation a : noConsumerAllocables) {
				noConsumerAllocations.addAllocation(a);
			}
			
			info.add(noConsumerAllocations);
		}
		
		return info;
	}

	private static Map<String, List<Allocation>> createConsumersMap(
			List<Allocation> allocations) {
		
		Map<String, List<Allocation>> consumersMap = new HashMap<String, List<Allocation>>();
		
		for (Allocation allocation : allocations) {
			String consumer = allocation.getConsumer();
			
			if(consumer != null) {//Some allocable may not have a consumer, the idle ones.
				if(! consumersMap.containsKey(consumer))  {
					consumersMap.put(consumer, new LinkedList<Allocation>());
				}
				List<Allocation> allocs = consumersMap.get(consumer);
				allocs.add(allocation);
			}
		}
		return consumersMap;
	}

	public static AllocationInfo getRequestInfo(
			List<AllocationInfo> infos, String consumer) {
		
		for(AllocationInfo info : infos){
			if(info.getConsumer().equals(consumer)){
				return info;
			}
		}
		
		return null;
	}

	public static boolean isThereAnIdleAllocation(List<AllocationInfo> allocations) {
		for (AllocationInfo allocationInfo : allocations) {
			if (allocationInfo.getConsumer() == null) {
				return true;
			}
		}
		return false;
	}

	public static Allocation takeNewerAllocation(AllocationInfo allocationInfo) {
		List<Allocation> allocations = allocationInfo.getAllocations();
		Collections.sort(allocations, new Comparator<Allocation>() {
			@Override
			public int compare(Allocation o1, Allocation o2) {
				return o1.getLastAssign() > o1.getLastAssign() ? 1 : -1;
			}
		});
		
		return allocations.isEmpty() ? null : allocations.remove(0);
	}

	/**
	 * @param consumer
	 * @param balances
	 * @return 0 for local consumers
	 */
	public static Double getPeerBalance(String consumer,
			Map<String, Double> balances) {
		if (balances == null) {
			return 0.;
		}
		
		Double balance = balances.get(consumer);
		return balance == null ? 0 : balance;
	}

	public static AllocationInfo getLeastNOFBalanced(
			List<AllocationInfo> allocations,
			final List<AllocationInfo> allAllocations, final AllocationInfo requestInfo, 
			final Map<String, Double> balances) {
		
		Collections.sort(allocations, new Comparator<AllocationInfo>() {
			public int compare(AllocationInfo o1, AllocationInfo o2) {
				Double prejudice1 = getPrejudice(o1, requestInfo, allAllocations, balances);
				Double prejudice2 = getPrejudice(o2, requestInfo, allAllocations, balances);
				
				if (!prejudice1.equals(prejudice2)) {
					return prejudice1.compareTo(prejudice2);
				}
				
				Double peerBalance1 = getPeerBalance(o1.getConsumer(), balances);
				Double peerBalance2 = getPeerBalance(o2.getConsumer(), balances);
				
				if (!peerBalance1.equals(peerBalance2)) {
					return getPeerBalance(o1.getConsumer(), balances).compareTo(
							getPeerBalance(o2.getConsumer(), balances));
				}
				
				Integer diff1 = o1.getBalance();
				Integer diff2 = o2.getBalance();
				
				
				if (!diff1.equals(diff2)) {
					return diff2.compareTo(diff1);
				}
				
				return compareNewerAllocations(o2, o1);
			}
		});
		
		return allocations.get(0);
	}

	protected static int compareNewerAllocations(AllocationInfo o2,
			AllocationInfo o1) {
		long newerTimeStamp1 = Long.MAX_VALUE;
		for (Allocation allocable : o1.getAllocations()) {
			if (allocable.getLastAssign() < newerTimeStamp1) {
				newerTimeStamp1 = allocable.getLastAssign();
			}
		}
		
		long newerTimeStamp2 = Long.MAX_VALUE;
		for (Allocation allocable : o2.getAllocations()) {
			if (allocable.getLastAssign() < newerTimeStamp2) {
				newerTimeStamp2 = allocable.getLastAssign();
			}
		}
		
		return newerTimeStamp1 > newerTimeStamp2 ? 1 : -1;
	}

	public static Double getPrejudice(AllocationInfo allocationToTake,
			AllocationInfo allocationToWin, List<AllocationInfo> allAllocations, 
			final Map<String, Double> balances) {
		double totalAllocations = 0;
		double totalPeerBalance = 0;
		
		for (AllocationInfo info : allAllocations) {
			totalAllocations += info.getAllocations().size();
			totalPeerBalance += getPeerBalance(info.getConsumer(), balances);
		}
		
		if (allocationToTake != null && allocationToWin == null) {
			totalAllocations--;
		}
		
		double totalPrejudice = 0;
		for (AllocationInfo info : allAllocations) {
			double balance = (totalPeerBalance > 0) ? getPeerBalance(info.getConsumer(), balances) / totalPeerBalance : totalPeerBalance;
			double allocations = info.getAllocations().size();
			
			if (info == allocationToTake) {
				allocations--;
			} else if (info == allocationToWin) {
				allocations++;
			}
			
			totalPrejudice += Math.abs(balance - (allocations / totalAllocations));
		}
		return round(totalPrejudice);
	}
	
	/**
	 * Rounds the decimal using 5 decimal digits
	 * @param numberToTrunc
	 * @return
	 */
	private static Double round(Double numberToTrunc) {
		return Math.round(numberToTrunc * 1E5) / 1E5;
	}

	public static void takeNeededWorkers(
			List<Allocation> possibleWorkersToAllocate, int allocationsLeft,
			List<Allocation> workersToAllocate) {
		
		List<Allocation> response = new LinkedList<Allocation>();
        
        Iterator<Allocation> iterator = possibleWorkersToAllocate.iterator();
        for (int i = 0; (i < allocationsLeft) && (iterator.hasNext()); i++) {
            response.add(iterator.next());
        }
        
        workersToAllocate.addAll(response);
	}

	public static PeerRequest getDownBalancedRequest(Peer peer) {

		List<PeerRequest> requests = getNeededRequests(peer);
		
		List<Allocation> allocationsRelatedToRequests = new LinkedList<Allocation>();
		for (Allocation allocation : peer.getAllocations()) {
			if (requests.contains(allocation.getRequest())) {
				allocationsRelatedToRequests.add(allocation);
			}
		}
		
		if (!allocationsRelatedToRequests.isEmpty()) {
			List<AllocationInfo> allocationInfo = generateAllocationInfo(allocationsRelatedToRequests, 
					allocationsRelatedToRequests.size(), 
					createConsumersMap(allocationsRelatedToRequests, requests));
			
			List<PeerRequest> lowerRequests = getDownBalancedRequests(
					allocationInfo, requests);
			return lowerRequests.iterator().next();
		}
		
		if (!requests.isEmpty()) {
			return requests.iterator().next();
		}
		
		return null;
	}

	public static List<PeerRequest> getNeededRequests(Peer peer) {
		List<PeerRequest> requests = peer.getRequests();
		Iterator<PeerRequest> iterator = requests.iterator();
		while (iterator.hasNext()) {
			PeerRequest request = iterator.next();
			if (request.isPaused() || request.getNeededWorkers() <= 0) {
				iterator.remove();
			}
		}
		return requests;
	}

	private static List<PeerRequest> getDownBalancedRequests(
			List<AllocationInfo> allocationInfo, final List<PeerRequest> requests) {

		List<PeerRequest> lowerRequests = new LinkedList<PeerRequest>();

		do {
			List<AllocationInfo> downBalancedAllocations = getDownBalancedConsumers(allocationInfo);

			boolean matched = false;

			for (AllocationInfo downBalancedAllocation : downBalancedAllocations) {
				PeerRequest request = getRequestByConsumer(requests, 
						downBalancedAllocation.getConsumer());

				if (request != null) {
					lowerRequests.add(request);
					matched = true;
				}
				allocationInfo.remove(downBalancedAllocation);
			}

			if (matched) {
				break;
			}

		} while (!allocationInfo.isEmpty());

		Comparator<PeerRequest> requestComparator = new Comparator<PeerRequest>() {
			public int compare(PeerRequest o1, PeerRequest o2) {
				return getPosition(o1) - getPosition(o2);
			}
			private int getPosition(PeerRequest o1) {
				return requests.indexOf(o1);
			}

		};

		Collections.sort(lowerRequests,	 requestComparator);

		return lowerRequests;
	}

	private static PeerRequest getRequestByConsumer(List<PeerRequest> requests,
			String consumer) {
		for (PeerRequest request : requests) {
			if(request.getConsumer().equals(consumer)) {
				return request;
			}
		}
		
		return null;
	}

	private static List<AllocationInfo> getDownBalancedConsumers(
			List<AllocationInfo> allocationInfo) {
		
		Collections.sort(allocationInfo, Collections.reverseOrder());
		
		List<AllocationInfo> result = new LinkedList<AllocationInfo>();
		
		if (allocationInfo != null && !allocationInfo.isEmpty()) {
			
			AllocationInfo lowerConsumer = allocationInfo.get(0);
			result.add(lowerConsumer);
			
			for (int i = 1; i < allocationInfo.size(); i++) {
				AllocationInfo currentConsumer = allocationInfo.get(i);

				if (currentConsumer.getBalance() == lowerConsumer.getBalance()) {
					result.add(currentConsumer);
				} else {
					break;
				}
			}
		}
		
		return result;
	}

	private static Map<String, List<Allocation>> createConsumersMap(
			List<Allocation> allocations,
			List<PeerRequest> requests) {
		Map<String, List<Allocation>> consumersMap = createConsumersMap(allocations);
		
		for (PeerRequest request : requests) {
			
			String consumer = request.getConsumer();
			
			if(!consumersMap.containsKey(consumer))  {
				consumersMap.put(consumer, new LinkedList<Allocation>());
			}
		}
		
		return consumersMap;
	}

}

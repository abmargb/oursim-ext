package br.edu.ufcg.lsd.oursim.events.peer.allocation;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.WorkerState;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class WorkerDistributionHelper {

	public static void redistributeWorker(long time, Peer peer, String workerId, OurSim ourSim) {
		
		Allocation workerAllocation = peer.getAllocation(workerId);
		
		if (workerAllocation == null) {
			return;
		}
		
		PeerRequest request = workerAllocation.getRequest();
		if (request != null) {
			request.removeAllocatedWorker(workerId);
		}
		
		workerAllocation.setRequest(null);
		workerAllocation.setConsumer(null);
		workerAllocation.setLastAssign(time);
		
		if (workerAllocation.isWorkerLocal()) {
			peer.setWorkerState(workerId, WorkerState.IDLE);
			redistributeLocalWorker(time, peer, workerId, ourSim);
		} else {
			redistributeRemoteWorker(time, peer, workerId, ourSim);
		}
		
			
		if (request != null && !request.isPaused() && request.getNeededWorkers() > 0) {
			request.setCancelled(false);
			Event requestWorkersEvent = ourSim
					.createEvent(
							PeerEvents.REQUEST_WORKERS,
							time
									+ ourSim.getLongProperty(Configuration.PROP_REQUEST_REPETITION_INTERVAL),
							peer.getId(), request.getSpec(), true);
			ourSim.addEvent(requestWorkersEvent);
		}
		
	}
	
	public static void redistributeIdleWorker(long time, Peer peer, String workerId, OurSim ourSim) {
		
		PeerRequest suitableRequestForWorker = AllocationHelper.getDownBalancedRequest(peer);
		if (suitableRequestForWorker != null) {
			allocateRequestToIdleWorker(time, peer, workerId,
					suitableRequestForWorker, ourSim);
		}
		
	}

	private static void redistributeLocalWorker(long time, Peer peer,
			String workerId, OurSim ourSim) {
		
		PeerRequest suitableRequestForWorker = AllocationHelper.getDownBalancedRequest(peer);
		
		if (suitableRequestForWorker == null) {
			ourSim.addNetworkEvent(ourSim.createEvent(WorkerEvents.STOP_WORKING, 
					time, workerId));
		} else {
			allocateRequestToIdleWorker(time, peer, workerId, suitableRequestForWorker, ourSim);
		}
	}

	private static void allocateRequestToIdleWorker(long time, Peer peer, String workerId,
			PeerRequest request, OurSim ourSim) {

		Allocation allocation = peer.getAllocation(workerId);
		
		allocation.setRequest(request);
		allocation.setConsumer(request.getConsumer());
		allocation.setConsumerLocal(true);
		allocation.setLastAssign(time);
		
		peer.setWorkerState(allocation.getWorker(), WorkerState.IN_USE);
		request.addAllocatedWorker(allocation.getWorker());
		
		ourSim.addNetworkEvent(ourSim.createEvent(WorkerEvents.WORK_FOR_BROKER, 
				time, request.getConsumer(), peer.getId(), request.getSpec(), workerId));
		
		if (request.isPaused() || request.getNeededWorkers() <= 0) {
			request.setPaused(true);
		}
	}

	public static void redistributeRemoteWorker(Long time, Peer peer,
			String worker, OurSim ourSim) {

		Allocation allocation = peer.getAllocation(worker);
		
		if (allocation == null || AllocationHelper.getNeededRequests(peer).isEmpty()) {
			peer.removeAllocation(worker);
			ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.DISPOSE_REMOTE_WORKER, 
					time, allocation.getProvider(), peer.getId(), worker));
			
			return;
		}
		
		PeerRequest request = AllocationHelper.getDownBalancedRequest(peer);
		
		allocation.setRequest(request);
		allocation.setConsumer(request.getConsumer());
		allocation.setConsumerLocal(true);
		allocation.setLastAssign(time);
		
		request.addAllocatedWorker(allocation.getWorker());
		
		ourSim.addNetworkEvent(ourSim.createEvent(WorkerEvents.WORK_FOR_BROKER, 
				time, request.getConsumer(), peer.getId(), request.getSpec(), allocation.getWorker()));
		
		if (request.getNeededWorkers() <= 0) {
			request.setCancelled(true);
		}
	}
	
}

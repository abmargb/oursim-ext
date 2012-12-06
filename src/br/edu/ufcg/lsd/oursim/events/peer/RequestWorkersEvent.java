package br.edu.ufcg.lsd.oursim.events.peer;

import java.util.List;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.allocation.AllocationHelper;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class RequestWorkersEvent extends AbstractEvent {

	private final String peerId;
	private final RequestSpec requestSpec;
	private final Boolean repetition;

	public RequestWorkersEvent(String peerId, 
			RequestSpec requestSpec, Boolean repetition) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
		this.requestSpec = requestSpec;
		this.repetition = repetition;
	}

	public RequestSpec getRequestSpec() {
		return requestSpec;
	}
	
	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		PeerRequest request = peer.getRequest(requestSpec.getId());
		
		if (request == null && !repetition) {
			request = new PeerRequest(requestSpec, 
					requestSpec.getBrokerId());
			peer.addRequest(request);
		}
		
		if (request == null || request.isPaused()) {
			return;
		}
		
		if (request.isCancelled()) {
			request.setCancelled(false);
			return;
		}
		
		List<Allocation> allocables = AllocationHelper.getAllocationsForLocalRequest(peer, request);
		
		for (Allocation allocable : allocables) {
			dispatchAllocation(request, peer, allocable, requestSpec.getBrokerId(), ourSim);
		}
		
		if (request.getNeededWorkers() > 0) {
			forwardRequestToCommunity(peer, request, ourSim);
			
			request.setCancelled(false);
			Event requestWorkersEvent = ourSim.createEvent(PeerEvents.REQUEST_WORKERS, 
					getTime() + ourSim.getLongProperty(
							Configuration.PROP_REQUEST_REPETITION_INTERVAL), 
					peerId, requestSpec, true);
			ourSim.addEvent(requestWorkersEvent);
		}
	}

	private void forwardRequestToCommunity(Peer peer, PeerRequest request, OurSim ourSim) {
		
		for (String workerProvider : peer.getWorkerProviders()) {
			if (workerProvider.equals(peerId)) {
				continue;
			}
			
			ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.REMOTE_REQUEST_WORKERS, 
					getTime(), peerId, workerProvider, request.getSpec()));
		}
		
	}

	private void dispatchAllocation(PeerRequest request, Peer peer, Allocation allocable, 
			String consumer, OurSim ourSim) {
		
		if (allocable.getConsumer() != null && allocable.isConsumerLocal()) {
			PeerRequest loserRequest = allocable.getRequest();
			loserRequest.removeAllocatedWorker(allocable.getWorker());
			
			if (!loserRequest.isPaused() && loserRequest.getNeededWorkers() > 0) {
				loserRequest.setCancelled(false);
				Event requestWorkersEvent = ourSim.createEvent(PeerEvents.REQUEST_WORKERS, 
						getTime() + ourSim.getLongProperty(
								Configuration.PROP_REQUEST_REPETITION_INTERVAL), 
						peerId, loserRequest.getSpec(), true);
				ourSim.addEvent(requestWorkersEvent);
			}
		}
		
		allocable.setConsumer(consumer);
		allocable.setRequest(request);
		allocable.setWorkerLocal(true);
		allocable.setConsumerLocal(true);
		allocable.setLastAssign(getTime());
		
		peer.setWorkerState(allocable.getWorker(), WorkerState.IN_USE);
		request.addAllocatedWorker(allocable.getWorker());
		
		ourSim.addNetworkEvent(ourSim.createEvent(WorkerEvents.WORK_FOR_BROKER, 
				getTime(), consumer, peer.getId(), request.getSpec(), allocable.getWorker()));
	}


}

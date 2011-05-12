package br.edu.ufcg.lsd.oursim.events.peer;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.broker.HereIsWorkerEvent;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class RequestWorkersEvent extends AbstractEvent {

	private final String peerId;
	private final RequestSpec requestSpec;
	private final boolean repetition;

	public RequestWorkersEvent(Long time, String peerId, 
			RequestSpec requestSpec, boolean repetition) {
		super(time, Event.DEF_PRIORITY, null);
		this.peerId = peerId;
		this.requestSpec = requestSpec;
		this.repetition = repetition;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		PeerRequest request = peer.getRequest(requestSpec.getId());
		
		if (request == null && !repetition) {
			request = new PeerRequest(requestSpec);
			peer.addRequest(request);
		}
		
		if (request == null || request.isPaused()) {
			return;
		}
		
		Set<String> allocableWorkers = getAllocableWorkers(peer, request);
		for (String workerId : allocableWorkers) {
			peer.setWorkerState(workerId, WorkerState.IN_USE);
			ourSim.addNetworkEvent(new HereIsWorkerEvent(getTime(), 
					workerId, requestSpec));
		}
		
		if (request.getNeededWorkers() > 0) {
			RequestWorkersEvent requestWorkersEvent = new RequestWorkersEvent(
					getTime() + ourSim.getLongProperty(
							Configuration.PROP_REQUEST_REPETITION_INTERVAL), 
					peerId, requestSpec, true);
			ourSim.addEvent(requestWorkersEvent);
		}
	}

	private Set<String> getAllocableWorkers(Peer peer, PeerRequest request) {
		Set<String> allocableWorkers = new HashSet<String>();
		for (String workerId : peer.getWorkersIds()) {
			if (peer.getWorkerState(workerId).equals(WorkerState.IDLE)) {
				allocableWorkers.add(workerId);
				request.addAllocatedWorker(workerId);
			}
			
			if (allocableWorkers.size() >= requestSpec.getRequiredWorkers()) {
				break;
			}
		}
		
		return allocableWorkers;
	}

}

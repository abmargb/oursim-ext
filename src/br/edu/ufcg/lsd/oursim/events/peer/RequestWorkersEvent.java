package br.edu.ufcg.lsd.oursim.events.peer;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.job.Request;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.broker.HereIsWorkerEvent;

public class RequestWorkersEvent extends AbstractEvent {

	private final Request request;
	private final String peerId;

	public RequestWorkersEvent(Long time, String peerId, Request request) {
		super(time, Event.DEF_PRIORITY, null);
		this.peerId = peerId;
		this.request = request;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		Set<String> allocableWorkers = getAllocableWorkers(peer);
		for (String workerId : allocableWorkers) {
			peer.setWorkerState(workerId, WorkerState.IN_USE);
			ourSim.addNetworkEvent(new HereIsWorkerEvent(getTime(), 
					workerId, request));
		}
	}

	private Set<String> getAllocableWorkers(Peer peer) {
		Set<String> allocableWorkers = new HashSet<String>();
		for (String workerId : peer.getWorkersIds()) {
			if (peer.getWorkerState(workerId).equals(WorkerState.IDLE)) {
				allocableWorkers.add(workerId);
			}
			
			if (allocableWorkers.size() >= request.getRequiredWorkers()) {
				break;
			}
		}
		
		return allocableWorkers;
	}

}

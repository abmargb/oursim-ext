package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class RemoteWorkerFailedEvent extends AbstractEvent {

	private final String consumer;
	private final String worker;

	public RemoteWorkerFailedEvent(String consumer, String worker) {
		super(Event.DEF_PRIORITY);
		this.consumer = consumer;
		this.worker = worker;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(consumer);
		if (!peer.isUp()) {
			return;
		}
		
		Allocation allocation = peer.getAllocation(worker);
		String provider = null;
		
		if (allocation != null) {
			PeerRequest request = allocation.getRequest();
			if (request != null) {
				request.removeAllocatedWorker(worker);
				scheduleRequest(ourSim, peer, request);
			}
			peer.removeAllocation(worker);
			provider = allocation.getProvider();
		} else {
			provider = peer.removeNotRecoveredRemoteWorker(worker);
		}
		
		if (provider != null) {
			ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.DISPOSE_REMOTE_WORKER, 
					getTime(), provider, consumer, worker));
		}
		
		ourSim.addEvent(ourSim.createEvent(FailureDetectionEvents.RELEASE, getTime(), 
				peer.getId(), worker));
	}

	private void scheduleRequest(OurSim ourSim, Peer peer, PeerRequest request) {
		if (!request.isPaused() && request.getNeededWorkers() > 0) {
			request.setCancelled(false);
			Event requestWorkersEvent = ourSim
					.createEvent(
							PeerEvents.REQUEST_WORKERS,
							getTime()
									+ ourSim.getLongProperty(Configuration.PROP_REQUEST_REPETITION_INTERVAL),
							peer.getId(), request.getSpec(), true);
			ourSim.addEvent(requestWorkersEvent);
		}
	}

}

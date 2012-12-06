package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class ResumeRequestEvent extends AbstractEvent {

	private final String peerId;
	private final RequestSpec requestSpec;

	public ResumeRequestEvent(RequestSpec requestSpec, String peerId) {
		super(Event.DEF_PRIORITY);
		this.requestSpec = requestSpec;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		PeerRequest request = peer.getRequest(requestSpec.getId());
		if (request == null) {
			return;
		}
		
		request.setPaused(false);
		
		if (request.getNeededWorkers() > 0) {
			request.setCancelled(false);
			Event requestWorkersEvent = ourSim.createEvent(PeerEvents.REQUEST_WORKERS, 
					getTime() + ourSim.getLongProperty(
							Configuration.PROP_REQUEST_REPETITION_INTERVAL), 
					peerId, requestSpec, true);
			ourSim.addEvent(requestWorkersEvent);
		}
	}

}

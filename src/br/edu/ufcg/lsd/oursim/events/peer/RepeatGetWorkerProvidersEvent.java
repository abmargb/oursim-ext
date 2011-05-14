package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class RepeatGetWorkerProvidersEvent extends AbstractEvent {

	private final String peerId;

	public RepeatGetWorkerProvidersEvent(Long time, String peerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(peerId);
		
		ourSim.addNetworkEvent(ourSim.createEvent(
				DiscoveryServiceEvents.GET_WORKER_PROVIDERS, 
				getTime(), peerId, peer.getDiscoveryServiceId()));
		
		ourSim.addEvent(ourSim.createEvent(
				PeerEvents.REPEAT_GET_WORKER_PROVIDERS, 
				getTime() + ourSim.getLongProperty(Configuration.PROP_GET_PROVIDERS_REPETITION_INTERVAL), 
				peerId));
	}

}

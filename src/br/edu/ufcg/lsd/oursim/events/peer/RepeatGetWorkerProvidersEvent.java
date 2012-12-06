package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class RepeatGetWorkerProvidersEvent extends AbstractEvent {

	private final String peerId;
	private final String dsId;

	public RepeatGetWorkerProvidersEvent(String peerId, String dsId) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
		this.dsId = dsId;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		if (peer.getMonitor(dsId).isUp()) {
			
			ourSim.addNetworkEvent(ourSim.createEvent(
					DiscoveryServiceEvents.GET_WORKER_PROVIDERS, 
					getTime(), peerId, peer.getDiscoveryServiceId()));
			
			ourSim.addEvent(ourSim.createEvent(
					PeerEvents.REPEAT_GET_WORKER_PROVIDERS, 
					getTime() + ourSim.getLongProperty(Configuration.PROP_GET_PROVIDERS_REPETITION_INTERVAL), 
					peerId, dsId));
			
		}
		
	}

}

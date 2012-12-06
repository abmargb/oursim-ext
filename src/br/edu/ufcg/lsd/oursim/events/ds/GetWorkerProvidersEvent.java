package br.edu.ufcg.lsd.oursim.events.ds;

import java.util.Set;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.DiscoveryService;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;

public class GetWorkerProvidersEvent extends AbstractEvent {

	private final String peerId;
	private final String dsId;

	public GetWorkerProvidersEvent(String peerId, String dsId) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
		this.dsId = dsId;
	}

	@Override
	public void process(OurSim ourSim) {
		DiscoveryService discoveryService = ourSim.getGrid().getObject(dsId);
		if (!discoveryService.isUp()) {
			return;
		}
		
		boolean modified = discoveryService.addPeer(peerId);
		
		Set<String> allPeers = discoveryService.getPeers();
		
		ourSim.addNetworkEvent(ourSim.createEvent(
				PeerEvents.HERE_ARE_WORKER_PROVIDERS, 
				getTime(), peerId, allPeers));
		
		if (modified) {
			for (String eachPeerId : allPeers) {
				if (!eachPeerId.equals(peerId)) {
					ourSim.addNetworkEvent(ourSim.createEvent(
							PeerEvents.HERE_ARE_WORKER_PROVIDERS, 
							getTime(), eachPeerId, allPeers));
				}
			}
		}
	}

}

package br.edu.ufcg.lsd.oursim.events.peer;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class HereAreWorkerProvidersEvent extends AbstractEvent {

	private final String peerId;
	private final Set<String> providers;

	public HereAreWorkerProvidersEvent(String peerId, HashSet<String> providers) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
		this.providers = providers;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		peer.setWorkerProviders(providers);
	}

}

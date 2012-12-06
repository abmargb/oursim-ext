package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerDonatedEvent extends AbstractEvent {

	private final String workerId;
	private final String consumerId;
	private final String provider;

	public WorkerDonatedEvent(String consumerId, String provider, String workerId) {
		super(Event.DEF_PRIORITY);
		this.consumerId = consumerId;
		this.provider = provider;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(provider);
		if (!peer.isUp()) {
			return;
		}
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.REMOTE_HERE_IS_WORKER, getTime(), 
				consumerId, provider, workerId));
	}

}

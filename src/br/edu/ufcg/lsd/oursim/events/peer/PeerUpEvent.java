package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.DiscoveryService;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class PeerUpEvent extends ActiveEntityUpEvent {

	public PeerUpEvent(String data) {
		super(Event.DEF_PRIORITY, data);
	}

	@Override
	protected void entityUp(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(getData());
		
		peer.addOnRecoveryEvent(DiscoveryService.class, 
				PeerEvents.REPEAT_GET_WORKER_PROVIDERS);
		peer.addOnFailureEvent(DiscoveryService.class, 
				PeerEvents.DISCOVERY_SERVICE_FAILED);
		
		peer.addOnRecoveryEvent(Worker.class, PeerEvents.WORKER_AVAILABLE);
		peer.addOnFailureEvent(Worker.class, PeerEvents.WORKER_FAILED);
		
		peer.addOnRecoveryEvent(Broker.class, PeerEvents.BROKER_AVAILABLE);
		peer.addOnFailureEvent(Broker.class, PeerEvents.BROKER_FAILED);
		
		if (peer.getDiscoveryServiceId() != null) {
			MonitorUtil.registerMonitored(ourSim, getTime(), 
					peer.getId(), peer.getDiscoveryServiceId());
		}
		
		for (String workerId : peer.getWorkersIds()) {
			MonitorUtil.registerMonitored(ourSim, getTime(), 
					peer.getId(), workerId);
		}
	}

}

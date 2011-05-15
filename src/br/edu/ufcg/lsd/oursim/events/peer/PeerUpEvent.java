package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class PeerUpEvent extends ActiveEntityUpEvent {

	public PeerUpEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}

	@Override
	protected void entityUp(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(getData());
		
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				peer.getId(), peer.getDiscoveryServiceId(), 
				ourSim.createEvent(PeerEvents.REPEAT_GET_WORKER_PROVIDERS, getTime(), peer.getId()), 
				ourSim.createEvent(PeerEvents.DISCOVERY_SERVICE_FAILED, getTime(), peer.getId()));
		
		for (String workerId : peer.getWorkersIds()) {
			MonitorUtil.registerMonitored(ourSim, getTime(), 
					peer.getId(), workerId, 
					ourSim.createEvent(WorkerEvents.SET_PEER, getTime(), workerId, peer.getId()),
					ourSim.createEvent(PeerEvents.WORKER_UNAVAILABLE, getTime(), workerId, peer.getId()));
		}
	}

}

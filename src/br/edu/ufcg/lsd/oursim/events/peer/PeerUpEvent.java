package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class PeerUpEvent extends ActiveEntityUpEvent {

	public static final String TYPE = "PEER_UP";
	
	public PeerUpEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}

	@Override
	protected void entityUp(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(getData());
		
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				peer.getId(), peer.getDiscoveryServiceId(), 
				new DiscoveryServiceAvailableEvent(getTime()), 
				new DiscoveryServiceFailedEvent(getTime()));
		
		for (String workerId : peer.getWorkersIds()) {
			MonitorUtil.registerMonitored(ourSim, getTime(), 
					peer.getId(), workerId, 
					new WorkerIdleEvent(getTime(), workerId, peer.getId()),
					new WorkerUnavailableEvent(getTime(), workerId, peer.getId()));
		}
	}

}

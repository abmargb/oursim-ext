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
	public void process(OurSim ourSim) {
		super.process(ourSim);
		
		Peer peer = (Peer) ourSim.getGrid().getObject(getData());
		
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				peer.getId(), peer.getDiscoveryServiceId(), 
				new DiscoveryServiceAvailableEvent(getTime()), 
				new DiscoveryServiceFailedEvent(getTime()));
		
		for (String workerId : peer.getWorkersIds()) {
			MonitorUtil.registerMonitored(ourSim, getTime(), 
					peer.getId(), workerId, 
					new WorkerAvailableEvent(getTime(), workerId, peer.getId()),
					new WorkerFailedEvent(getTime(), workerId, peer.getId()));
		}
	}

}

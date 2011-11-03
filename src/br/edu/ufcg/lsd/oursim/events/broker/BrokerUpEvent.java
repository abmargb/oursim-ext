package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class BrokerUpEvent extends ActiveEntityUpEvent {

	public BrokerUpEvent(String data) {
		super(Event.DEF_PRIORITY, data);
	}
	
	@Override
	protected void entityUp(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(getData());
		
		broker.addOnRecoveryEvent(Peer.class, BrokerEvents.PEER_AVAILABLE);
		broker.addOnFailureEvent(Peer.class, BrokerEvents.PEER_FAILED);
		
		broker.addOnRecoveryEvent(Worker.class, BrokerEvents.WORKER_AVAILABLE); 
		broker.addOnFailureEvent(Worker.class, BrokerEvents.WORKER_FAILED);
		
		if (broker.getPeerId() != null) {
			MonitorUtil.registerMonitored(ourSim, getTime(), 
					broker.getId(), broker.getPeerId());
		}
		
	}

}

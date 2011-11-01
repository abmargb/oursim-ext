package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class BrokerUpEvent extends ActiveEntityUpEvent {

	public BrokerUpEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}
	
	@Override
	protected void entityUp(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(getData());
		
		if (broker.getPeerId() != null) {
			MonitorUtil.registerMonitored(ourSim, getTime(), 
					broker.getId(), broker.getPeerId(), 
					ourSim.createEvent(BrokerEvents.PEER_AVAILABLE, getTime(), broker.getId()), 
					ourSim.createEvent(BrokerEvents.PEER_FAILED, getTime()));
		}
		
	}

}

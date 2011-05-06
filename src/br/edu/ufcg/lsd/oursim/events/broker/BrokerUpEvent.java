package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class BrokerUpEvent extends ActiveEntityUpEvent {

	public static final String TYPE = "BROKER_UP";

	public BrokerUpEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}
	
	@Override
	protected void entityUp(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(getData());
		
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				broker.getId(), broker.getPeerId(), 
				new PeerAvailableEvent(getTime(), broker.getId()), 
				new PeerFailedEvent(getTime()));
	}

}

package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.PrimaryEvent;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;
import br.edu.ufcg.lsd.oursim.util.LineParser;

public class SetGridEvent extends PrimaryEvent {

	public SetGridEvent(String data) {
		super(Event.DEF_PRIORITY, data);
	}

	@Override
	public void process(OurSim ourSim) {
		LineParser lineParser = new LineParser(getData());
		String brokerId = lineParser.next();
		String peerId = lineParser.next();
		
		Broker broker = ourSim.getGrid().getObject(brokerId);
		broker.setPeerId(peerId);
		
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				brokerId, peerId, 
				ourSim.createEvent(BrokerEvents.PEER_AVAILABLE, getTime(), brokerId), 
				ourSim.createEvent(BrokerEvents.PEER_FAILED, getTime()));
	}

}

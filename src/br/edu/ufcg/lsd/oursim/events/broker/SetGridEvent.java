package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.PrimaryEvent;
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;
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
		
		if (!broker.isUp()) {
			return;
		}
		
		String oldPeerId = broker.getPeerId();
		
		if (oldPeerId != null) {
			if (oldPeerId.equals(peerId)) {
				return;
			} else {
				ourSim.addEvent(ourSim.createEvent(FailureDetectionEvents.RELEASE, getTime(), 
						broker.getId(), oldPeerId));
			}
		}
		
		broker.setPeerId(peerId);
		
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				brokerId, peerId);
	}

}

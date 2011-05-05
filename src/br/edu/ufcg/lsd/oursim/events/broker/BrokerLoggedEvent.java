package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class BrokerLoggedEvent extends AbstractEvent {

	private String brokerId;

	public BrokerLoggedEvent(Long time, String brokerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		ourSim.addEvent(new BrokerScheduleEvent(getTime() + 1, brokerId));
	}


}

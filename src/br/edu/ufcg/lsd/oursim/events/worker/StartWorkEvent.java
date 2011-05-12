package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;

public class StartWorkEvent extends AbstractEvent {

	private final Replica replica;

	public StartWorkEvent(Long time, Replica replica) {
		super(time, Event.DEF_PRIORITY, null);
		this.replica = replica;
	}

	@Override
	public void process(OurSim ourSim) {
		ourSim.addNetworkEvent(ourSim.createEvent(BrokerEvents.HERE_IS_EXECUTION_RESULT, 
				getTime() + replica.getTask().getDuration(), 
				replica));
	}

}

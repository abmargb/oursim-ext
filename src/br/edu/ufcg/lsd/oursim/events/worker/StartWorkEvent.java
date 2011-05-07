package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.broker.HereIsExecutionResultEvent;

public class StartWorkEvent extends AbstractEvent {

	private final Replica replica;
	private final String brokerId;

	public StartWorkEvent(Long time, String brokerId, Replica replica) {
		super(time, Event.DEF_PRIORITY, null);
		this.brokerId = brokerId;
		this.replica = replica;
	}

	@Override
	public void process(OurSim ourSim) {
		ourSim.addNetworkEvent(new HereIsExecutionResultEvent(
				getTime() + replica.getTask().getDuration(), 
				brokerId, replica));
	}

}

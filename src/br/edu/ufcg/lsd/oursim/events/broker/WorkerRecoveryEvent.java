package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Request;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class WorkerRecoveryEvent extends AbstractEvent {

	private final Request request;
	private final String workerId;

	public WorkerRecoveryEvent(Long time, Request request, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.request = request;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(request.getBrokerId());
		broker.workerIsAvailable(workerId);
		
		if (!broker.isScheduled()) {
			broker.setScheduled(true);
			ourSim.addEvent(
					new BrokerScheduleEvent(getTime() + ourSim.getLongProperty(Configuration.PROP_BROKER_SCHEDULER_INTERVAL), 
							request.getBrokerId()));
			
		}
	}

}

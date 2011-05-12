package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerRecoveryEvent extends AbstractEvent {

	private final BrokerRequest request;
	private final String workerId;

	public WorkerRecoveryEvent(Long time, BrokerRequest request, String workerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.request = request;
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(request.getSpec().getBrokerId());
		request.getJob().workerIsAvailable(workerId);
		
		SchedulerHelper.updateScheduler(ourSim, broker, getTime());
	}

}

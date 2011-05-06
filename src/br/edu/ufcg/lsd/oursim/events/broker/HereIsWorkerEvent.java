package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Request;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class HereIsWorkerEvent extends AbstractEvent {

	private final Request request;
	private final String workerId;

	public HereIsWorkerEvent(Long time, String workerId, Request request) {
		super(time, Event.DEF_PRIORITY, null);
		this.workerId = workerId;
		this.request = request;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Broker broker = ourSim.getGrid().getObject(request.getBrokerId());
		
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				broker.getId(), workerId, 
				new WorkerRecoveryEvent(getTime(), request, workerId), 
				new WorkerFailedEvent(getTime(), request, workerId));
	}


}

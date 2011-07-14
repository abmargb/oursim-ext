package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class HereIsWorkerEvent extends AbstractEvent {

	private final RequestSpec requestSpec;
	private final String workerId;

	public HereIsWorkerEvent(Long time, String workerId, RequestSpec requestSpec) {
		super(time, Event.DEF_PRIORITY);
		this.workerId = workerId;
		this.requestSpec = requestSpec;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Broker broker = ourSim.getGrid().getObject(requestSpec.getBrokerId());
		BrokerRequest request = broker.getRequest(requestSpec.getId());
		
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				broker.getId(), workerId, 
				ourSim.createEvent(BrokerEvents.WORKER_RECOVERY, getTime(), request, workerId), 
				ourSim.createEvent(BrokerEvents.WORKER_FAILED, getTime(), request, workerId));
	}


}

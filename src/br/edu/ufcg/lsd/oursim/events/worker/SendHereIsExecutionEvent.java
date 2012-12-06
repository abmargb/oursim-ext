package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.entities.job.Replica;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;

public class SendHereIsExecutionEvent extends AbstractEvent {

	private final Replica replica;
	private final String workerId;
	private final String brokerId;

	public SendHereIsExecutionEvent(Replica replica, String workerId, String brokerId) {
		super(Event.DEF_PRIORITY);
		this.replica = replica;
		this.workerId = workerId;
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		
		if (!worker.isUp()) {
			return;
		}
		
		if (worker.getConsumer() == null) {
			return;
		}
		
		ourSim.addNetworkEvent(ourSim.createEvent(BrokerEvents.HERE_IS_EXECUTION_RESULT, 
				getTime(), replica, brokerId));
		
		WorkAccounting workAccounting = worker.getCurrentWorkAccounting();
		if (worker.getRemotePeer() != null && workAccounting != null) {
			if (workAccounting.getInitCPUtime() != null) {
				workAccounting.setCPUTime(getTime() - workAccounting.getInitCPUtime());
			}
			worker.addWorkAccounting(workAccounting);
			worker.setCurrentWorkAccounting(null);
		}
		
	}

}

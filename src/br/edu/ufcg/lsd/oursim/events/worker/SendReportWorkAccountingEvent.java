package br.edu.ufcg.lsd.oursim.events.worker;

import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class SendReportWorkAccountingEvent extends AbstractEvent {

	private final String workerId;

	public SendReportWorkAccountingEvent(String workerId) {
		super(Event.DEF_PRIORITY);
		this.workerId = workerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		
		if (!worker.isUp()) {
			return;
		}
		
		WorkAccounting currentWorkAccounting = worker.getCurrentWorkAccounting();

		if (currentWorkAccounting != null) {
			
			if (currentWorkAccounting.getInitCPUtime() != null) {
				currentWorkAccounting.setCPUTime(
						getTime() - currentWorkAccounting.getInitCPUtime());
			}
			
			WorkAccounting workAccounting = new WorkAccounting(
					currentWorkAccounting.getWorkerId(), 
					currentWorkAccounting.getRemotePeerId());
			workAccounting.setCPUTime(currentWorkAccounting.getCPUTime());
			workAccounting.setInitCPUtime(currentWorkAccounting.getInitCPUtime());
			
			worker.addWorkAccounting(workAccounting);
			
			currentWorkAccounting.setCPUTime(0);
			currentWorkAccounting.setInitCPUtime(getTime());
		}
		
		List<WorkAccounting> workAccountings = new LinkedList<WorkAccounting>(
				worker.getWorkAccountings());
		if (!workAccountings.isEmpty()) {
			
			ourSim.addNetworkEvent(ourSim.createEvent(
					PeerEvents.REPORT_WORK_ACCOUNTING, 
					getTime(), worker.getPeer(), 
					workAccountings));
			
			worker.clearWorkAccountings();
		}

		ourSim.addEvent(ourSim.createEvent(
				WorkerEvents.SEND_REPORT_WORK_ACCOUNTING, 
				getTime() + ourSim.getLongProperty(
						Configuration.PROP_REPORT_WORK_ACCOUNTING_REPETITION_INTERVAL), 
						workerId));
		
	}

}

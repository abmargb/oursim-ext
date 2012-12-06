package br.edu.ufcg.lsd.oursim.events.peer;

import java.util.LinkedList;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.accounting.AccountingHelper;

public class ReportWorkAccountingEvent extends AbstractEvent {

	private final String peerId;
	private final LinkedList<WorkAccounting> workAccountings;

	public ReportWorkAccountingEvent(String peerId, 
			LinkedList<WorkAccounting> workAccountings) {
		super(Event.DEF_PRIORITY);
		this.peerId = peerId;
		this.workAccountings = workAccountings;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		for (WorkAccounting workAccounting : workAccountings) {
			AccountingHelper.commitWorkerAccounting(peer, workAccounting);
		}
	}

}

package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class SetPeerEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;

	public SetPeerEvent(String workerId, String peerId) {
		super(Event.DEF_PRIORITY);
		this.workerId = workerId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Worker worker = ourSim.getGrid().getObject(workerId);
		
		if (!worker.isUp()) {
			return;
		}
		
		worker.setPeer(peerId);
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.WORKER_IDLE, 
				getTime(), workerId, peerId));
		
		ourSim.addEvent(ourSim.createEvent(
				WorkerEvents.SEND_REPORT_WORK_ACCOUNTING, 
				getTime() + ourSim.getLongProperty(
						Configuration.PROP_REPORT_WORK_ACCOUNTING_REPETITION_INTERVAL), 
						workerId));
	}

}

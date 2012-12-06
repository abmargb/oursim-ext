package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class HereIsRemoteWorkerEvent extends AbstractEvent {

	private final String consumer;
	private final String worker;
	private final String provider;

	public HereIsRemoteWorkerEvent(String consumer, 
			String provider, String worker) {
		super(Event.DEF_PRIORITY);
		this.consumer = consumer;
		this.provider = provider;
		this.worker = worker;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(consumer);
		if (!peer.isUp()) {
			return;
		}
		
		peer.addNotRecoveredRemoteWorker(worker, provider);
		
		MonitorUtil.registerMonitored(ourSim, getTime(), consumer, worker);
	}

}

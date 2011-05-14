package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.Monitor;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class RemoteHereIsWorkerEvent extends AbstractEvent {

	private final String consumer;
	private final RequestSpec requestSpec;
	private final String worker;
	private final String provider;

	public RemoteHereIsWorkerEvent(Long time, String consumer, 
			String provider, RequestSpec requestSpec, String worker) {
		super(time, Event.DEF_PRIORITY, null);
		this.consumer = consumer;
		this.provider = provider;
		this.worker = worker;
		this.requestSpec = requestSpec;
	}

	@Override
	public void process(OurSim ourSim) {
	
		MonitorUtil.registerMonitored(ourSim, getTime(), consumer, worker, 
				ourSim.createEvent(PeerEvents.REMOTE_WORKER_RECOVERY, getTime(), 
						consumer, provider, requestSpec, worker), 
				ourSim.createEvent(PeerEvents.REMOTE_WORKER_FAILURE, getTime(), 
						consumer, provider, requestSpec, worker));
		
	}

}

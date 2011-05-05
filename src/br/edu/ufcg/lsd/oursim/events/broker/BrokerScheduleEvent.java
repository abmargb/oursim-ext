package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.RequestWorkersEvent;

public class BrokerScheduleEvent extends AbstractEvent {

	private String brokerId;

	public BrokerScheduleEvent(Long time, String brokerId) {
		super(time, Event.HIGHER_PRIORITY, null);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = (Broker) ourSim.getGrid().getObject(brokerId);
		
		for (Job job : broker.getJobs()) {
			int workersToRequest = needToRequest(job);
			if (workersToRequest > 0) {
				ourSim.addNetworkEvent(new RequestWorkersEvent(getTime(), 
						brokerId, workersToRequest));
			}
		}
	}

	private int needToRequest(Job job) {
		return 0;
	}


}

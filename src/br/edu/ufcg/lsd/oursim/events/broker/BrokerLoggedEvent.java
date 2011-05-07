package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.Random;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Request;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.RequestWorkersEvent;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class BrokerLoggedEvent extends AbstractEvent {

	private String brokerId;

	public BrokerLoggedEvent(Long time, String brokerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		for (Job job : broker.getJobs()) {
			if (!SchedulerHelper.isJobSatisfied(job, ourSim)) {
				Request request = new Request();
				request.setBrokerId(brokerId);
				request.setId(new Random().nextLong());
				request.setJob(job);
				request.setRequiredWorkers(job.getTasks().size()
						* ourSim.getIntProperty(Configuration.PROP_BROKER_MAX_REPLICAS));
				job.setRequest(request);
				
				ourSim.addNetworkEvent(new RequestWorkersEvent(getTime(), 
						broker.getPeerId(), request));
			}
		}
	}


}

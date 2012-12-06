package br.edu.ufcg.lsd.oursim.events.broker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class BrokerLoggedEvent extends AbstractEvent {

	private String brokerId;

	public BrokerLoggedEvent(String brokerId) {
		super(Event.DEF_PRIORITY);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		if (!broker.isUp()) {
			return;
		}
		
		for (Job job : broker.getJobs()) {
			if (!SchedulerHelper.isJobSatisfied(job, ourSim)) {
				RequestSpec requestSpec = new RequestSpec();
				requestSpec.setBrokerId(brokerId);
				requestSpec.setId(Math.abs(ourSim.getRandom().nextLong()));
				requestSpec.setRequiredWorkers(job.getTasks().size()
						* ourSim.getIntProperty(Configuration.PROP_BROKER_MAX_REPLICAS));
				
				BrokerRequest request = new BrokerRequest(requestSpec);
				request.setJob(job);
				job.setRequest(request);
				broker.addRequest(request);
				
				ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.REQUEST_WORKERS, getTime(), 
						broker.getPeerId(), request.getSpec(), false));
			}
		}
	}

}

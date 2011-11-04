package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.List;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.MonitorUtil;

public class HereIsWorkerEvent extends AbstractEvent {

	private final RequestSpec requestSpec;
	private final String workerId;

	public HereIsWorkerEvent(String workerId, RequestSpec requestSpec) {
		super(Event.DEF_PRIORITY);
		this.workerId = workerId;
		this.requestSpec = requestSpec;
	}

	@Override
	public void process(OurSim ourSim) {
		
		Broker broker = ourSim.getGrid().getObject(requestSpec.getBrokerId());
		BrokerRequest request = broker.getRequest(requestSpec.getId());
		
		if (!hasJobRunning(broker)) {
			SchedulerHelper.disposeWorker(
					request.getJob(), broker, workerId, ourSim, getTime());
			return;
		}
		
		request.getJob().workerIsNotRecovered(workerId);
		MonitorUtil.registerMonitored(ourSim, getTime(), 
				broker.getId(), workerId);
	}

	private boolean hasJobRunning(Broker broker) {
		List<Job> jobs = broker.getJobs();
		for (Job job : jobs) {
			if (!SchedulerHelper.isJobEnded(job)) {
				return true;
			}
		}
		return false;
	}


}

package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;

public class CleanWorkerHelper {

	public static void cleanWorker(long time, Worker worker, boolean releaseRemotePeer, OurSim ourSim) {
		
		String oldConsumer = worker.getConsumer();
		if (oldConsumer != null ) {
			ourSim.addEvent(ourSim.createEvent(FailureDetectionEvents.RELEASE, time, 
					worker.getId(), oldConsumer));
			worker.setConsumer(null);
		}
		
		WorkAccounting workAccounting = worker.getCurrentWorkAccounting();
		if (worker.getRemotePeer() != null && workAccounting != null) {
			workAccounting.setCPUTime(time - workAccounting.getInitCPUtime());
			worker.addWorkAccounting(workAccounting);
			worker.setCurrentWorkAccounting(null);
		}
		
		if (releaseRemotePeer && worker.getRemotePeer() != null) {
			ourSim.addEvent(ourSim.createEvent(FailureDetectionEvents.RELEASE, time, 
					worker.getId(), worker.getRemotePeer()));
			worker.setRemotePeer(null);
		}
	}
	
}

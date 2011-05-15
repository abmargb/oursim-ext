package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;

public class CleanWorkerHelper {

	public static void cleanWorker(long time, Worker worker, boolean releaseRemotePeer) {
		
		String oldConsumer = worker.getConsumer();
		if (oldConsumer != null ) {
			worker.release(oldConsumer);
			worker.setConsumer(null);
		}
		
		WorkAccounting workAccounting = worker.getCurrentWorkAccounting();
		if (worker.getRemotePeer() != null && workAccounting != null) {
			workAccounting.setCPUTime(time - workAccounting.getInitCPUtime());
			worker.addWorkAccounting(workAccounting);
			worker.setCurrentWorkAccounting(null);
		}
		
		if (releaseRemotePeer && worker.getRemotePeer() != null) {
			worker.release(worker.getRemotePeer());
			worker.setRemotePeer(null);
		}
	}
	
}

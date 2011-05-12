package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.entities.grid.Peer;

public class WorkerDistributionHelper {

	public static void redistributeWorker(Peer peer, String workerId) {
		peer.setWorkerState(workerId, WorkerState.IDLE);
	}
	
}

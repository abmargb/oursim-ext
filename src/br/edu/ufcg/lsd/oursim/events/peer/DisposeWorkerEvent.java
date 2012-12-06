package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.allocation.WorkerDistributionHelper;

public class DisposeWorkerEvent extends AbstractEvent {

	private final String workerId;
	private final String peerId;
	private final Long requestId;

	public DisposeWorkerEvent(String workerId, Long requestId, String peerId) {
		super(Event.DEF_PRIORITY);
		this.workerId = workerId;
		this.requestId = requestId;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		Allocation workerAllocation = peer.getAllocation(workerId);
		
		if (workerAllocation == null || workerAllocation.getConsumer() == null
				|| workerAllocation.getRequest() == null
				|| requestId != workerAllocation.getRequest().getSpec().getId()) {
			return;
		}
		
		WorkerDistributionHelper.redistributeWorker(getTime(), peer, workerId, ourSim);
	}

}

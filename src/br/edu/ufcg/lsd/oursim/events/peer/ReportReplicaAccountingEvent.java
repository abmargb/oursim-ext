package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.accounting.ReplicaAccounting;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class ReportReplicaAccountingEvent extends AbstractEvent {

	private final ReplicaAccounting replicaAccounting;
	private final String peerId;

	public ReportReplicaAccountingEvent(ReplicaAccounting replicaAccounting, String peerId) {
		super(Event.DEF_PRIORITY);
		this.replicaAccounting = replicaAccounting;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		if (!peer.isUp()) {
			return;
		}
		
		Allocation allocation = peer.getAllocation(replicaAccounting.getWorkerId());
		
		if (allocation == null) {
			return;
		}
		
		replicaAccounting.setProvider(allocation.getProvider());
		peer.addReplicaAccounting(replicaAccounting);
	}

}

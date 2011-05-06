package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.events.peer.WorkerState;

public class Peer extends ActiveEntity {

	private Map<String, WorkerState> workersStates = new HashMap<String, WorkerState>();
	private Set<String> brokersIds = new HashSet<String>();
	
	private String dsId;
	
	public void addWorker(String workerId) {
		workersStates.put(workerId, WorkerState.UNAVAILABLE);
	}
	
	public Set<String> getWorkersIds() {
		return workersStates.keySet();
	}
	
	public void addBroker(String brokerId) {
		brokersIds.add(brokerId);
	}
	
	public void setDiscoveryServiceId(String dsId) {
		this.dsId = dsId;
	}
	
	public String getDiscoveryServiceId() {
		return dsId;
	}

	public void setWorkerState(String workerId, WorkerState state) {
		workersStates.put(workerId, state);
	}

	public WorkerState getWorkerState(String workerId) {
		return workersStates.get(workerId);
	}
}

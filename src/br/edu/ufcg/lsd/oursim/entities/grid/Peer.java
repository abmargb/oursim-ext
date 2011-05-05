package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;

public class Peer extends ActiveEntity {

	private Map<String, String> workersStates = new HashMap<String, String>();
	private Set<String> brokersIds = new HashSet<String>();
	
	private String dsId;
	
	public void addWorker(String workerId) {
		workersStates.put(workerId, Worker.STATE_UNVAILABLE);
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

	public void setWorkerState(String workerId, String state) {
		workersStates.put(workerId, state);
	}
}

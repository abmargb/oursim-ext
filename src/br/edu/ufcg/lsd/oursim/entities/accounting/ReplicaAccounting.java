package br.edu.ufcg.lsd.oursim.entities.accounting;

import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;

public class ReplicaAccounting {

	private final long requestId;
	private final String workerId;
	private final String brokerId;
	private final long cpuTime;
	private final ExecutionState state;
	private String provider;
	/**
	 * @param requestId
	 * @param workerId
	 * @param brokerId
	 * @param cpuTime
	 * @param state
	 */
	public ReplicaAccounting(long requestId, String workerId, String brokerId,
			long cpuTime, ExecutionState state) {
		this.requestId = requestId;
		this.workerId = workerId;
		this.brokerId = brokerId;
		this.cpuTime = cpuTime;
		this.state = state;
	}
	
	public long getRequestId() {
		return requestId;
	}
	
	public String getWorkerId() {
		return workerId;
	}
	
	public String getBrokerId() {
		return brokerId;
	}
	
	public long getCpuTime() {
		return cpuTime;
	}
	
	public ExecutionState getState() {
		return state;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getProvider() {
		return provider;
	}
}

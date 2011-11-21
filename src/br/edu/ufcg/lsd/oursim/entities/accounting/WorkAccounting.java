package br.edu.ufcg.lsd.oursim.entities.accounting;

public class WorkAccounting {

	private final String workerId;
	private final String remotePeerId;
	private Long initCPUtime;
	private long cpuTime;
	
	/**
	 * @param workerId
	 * @param remotePeerId
	 * @param initCPUtime
	 */
	public WorkAccounting(String workerId, String remotePeerId) {
		this.workerId = workerId;
		this.remotePeerId = remotePeerId;
	}

	public String getWorkerId() {
		return workerId;
	}

	public String getRemotePeerId() {
		return remotePeerId;
	}

	public void setInitCPUtime(Long initCPUtime) {
		this.initCPUtime = initCPUtime;
	}
	
	public Long getInitCPUtime() {
		return initCPUtime;
	}

	public void setCPUTime(long cpuTime) {
		this.cpuTime = cpuTime;
	}

	public long getCPUTime() {
		return cpuTime;
	}
	
}

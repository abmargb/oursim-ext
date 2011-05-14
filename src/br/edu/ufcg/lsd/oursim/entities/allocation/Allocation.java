package br.edu.ufcg.lsd.oursim.entities.allocation;

import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;

public class Allocation {

	private final String workerId;
	private final String provider;
	private long lastAssign;
	private PeerRequest request;
	private String consumer;
	
	private boolean workerLocal = true;
	private boolean consumerLocal = true;

	/**
	 * @param workerId
	 */
	public Allocation(String workerId, String provider) {
		this.workerId = workerId;
		this.provider = provider;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Allocation)) {
			return false;
		}
		return this.workerId.equals(((Allocation)obj).workerId);
	}

	public boolean isWorkerLocal() {
		return workerLocal;
	}

	public void setLastAssign(long lastAssign) {
		this.lastAssign = lastAssign;
	}

	public long getLastAssign() {
		return lastAssign;
	}

	public String getWorker() {
		return workerId;
	}

	public void setWorkerLocal(boolean local) {
		this.workerLocal = local;
	}

	public void setRequest(PeerRequest request) {
		this.request = request;
	}

	public PeerRequest getRequest() {
		return request;
	}

	public void setConsumerLocal(boolean consumerLocal) {
		this.consumerLocal = consumerLocal;
	}

	public boolean isConsumerLocal() {
		return consumerLocal;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public String getConsumer() {
		return consumer;
	}

	public String getProvider() {
		return provider;
	}
	
	
}

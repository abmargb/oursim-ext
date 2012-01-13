package br.edu.ufcg.lsd.oursim.entities.request;

import java.util.HashSet;
import java.util.Set;

/**
 * Request representation in the Peer.
 * 
 * @see RequestSpec
 * 
 */
public class PeerRequest {

	private boolean paused;
	private boolean cancelled;
	private final RequestSpec spec;
	private final Set<String> allocatedWorkers = new HashSet<String>();
	private final String consumer;
	
	public PeerRequest(RequestSpec spec, String consumer) {
		this.spec = spec;
		this.consumer = consumer;
	}

	public String getConsumer() {
		return consumer;
	}
	
	public RequestSpec getSpec() {
		return spec;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * Gets the number of Workers needed by this request
	 * 
	 * @return
	 */
	public int getNeededWorkers() {
		// Max to avoid negative value, but a request must never receive more
		// workers then requested.
		return Math.max(this.getSpec().getRequiredWorkers()
				- this.allocatedWorkers.size(), 0);
	}

	public void addAllocatedWorker(String workerId) {
		this.allocatedWorkers.add(workerId);
	}

	public Set<String> getAllocatedWorkers() {
		return new HashSet<String>(allocatedWorkers);
	}

	public void removeAllocatedWorker(String workerId) {
		this.allocatedWorkers.remove(workerId);
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}
	
}

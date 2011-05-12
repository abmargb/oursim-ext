package br.edu.ufcg.lsd.oursim.entities.request;

/**
 * Request specification.
 * Contains an id (not necessarily sequential for a task),
 * the broker id and the number of required workers.
 * When considering WQR algorithm, requiredWorkers = noTask * maxReplicas
 * 
 */
public class RequestSpec {

	private long id;
	private int requiredWorkers;
	private String brokerId;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public int getRequiredWorkers() {
		return requiredWorkers;
	}
	
	public void setRequiredWorkers(int requiredWorkers) {
		this.requiredWorkers = requiredWorkers;
	}
	
	public String getBrokerId() {
		return brokerId;
	}
	
	public void setBrokerId(String brokerId) {
		this.brokerId = brokerId;
	}

}

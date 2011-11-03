package br.edu.ufcg.lsd.oursim.entities;



public class Monitor {

	private ActiveEntity object;
	private boolean isUp;
	private boolean creatingConnection = true;
	
	public Monitor(ActiveEntity monitoredObj) {
		object = monitoredObj;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}
	
	public boolean isUp() {
		return isUp;
	}

	public void setObject(ActiveEntity object) {
		this.object = object;
	}

	public ActiveEntity getObject() {
		return object;
	}

	public void setCreatingConnection(boolean creatingConnection) {
		this.creatingConnection = creatingConnection;
	}

	public boolean isCreatingConnection() {
		return creatingConnection;
	}
	
}

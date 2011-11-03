package br.edu.ufcg.lsd.oursim.events;


public abstract class PrimaryEvent extends AbstractEvent {

	private final String data;

	public PrimaryEvent(Integer priority, String data) {
		super(priority);
		this.data = data;
	}

	public String getData() {
		return data;
	}

}

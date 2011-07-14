package br.edu.ufcg.lsd.oursim.events;


public abstract class PrimaryEvent extends AbstractEvent {

	private final String data;

	public PrimaryEvent(Long time, Integer priority, String data) {
		super(time, priority);
		this.data = data;
	}

	public String getData() {
		return data;
	}

}

package br.edu.ufcg.lsd.oursim.events;

public class EventSpec {

	private final String type;
	private final long time;
	private final Object[] params;
	
	/**
	 * @param type
	 * @param time
	 * @param params
	 */
	public EventSpec(String type, long time, Object... params) {
		this.type = type;
		this.time = time;
		this.params = params;
	}
	
	public String getType() {
		return type;
	}
	
	public long getTime() {
		return time;
	}
	
	public Object[] getParams() {
		return params;
	}
}

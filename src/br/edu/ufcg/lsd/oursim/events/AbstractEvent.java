package br.edu.ufcg.lsd.oursim.events;

public abstract class AbstractEvent implements Event {

	private Long time;
	private Integer priority;
	private String data;
	
	/**
	 * @param time
	 * @param priority
	 * @param type
	 */
	public AbstractEvent(Long time, Integer priority, String data) {
		this.time = time;
		this.priority = priority;
		this.data = data;
	}
	
	public String getData() {
		return data;
	};
	
	@Override
	public void setTime(Long time) {
		this.time = time;
	}
	
	@Override
	public Long getTime() {
		return time;
	}
	
	@Override
	public Integer getPriority() {
		return priority;
	}
	
	@Override
	public int compareTo(Event o) {
		int timeCompare = this.getTime().compareTo(o.getTime());
		if (timeCompare != 0) {
			return timeCompare;
		}
		return o.getPriority().compareTo(this.getPriority());
	}
}

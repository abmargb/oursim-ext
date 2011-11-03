package br.edu.ufcg.lsd.oursim.events;

public abstract class AbstractEvent implements Event {

	private Long time;
	private Integer priority;
	private String type;
	
	/**
	 * @param priority
	 * @param type
	 */
	public AbstractEvent(Integer priority) {
		this.priority = priority;
	}
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

package br.edu.ufcg.lsd.oursim.queue;

import java.util.PriorityQueue;

import br.edu.ufcg.lsd.oursim.events.Event;

public class EventQueue extends PriorityQueue<Event>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6795297555525744590L;
	private static final int PAGE_SIZE = 100;
	
	private final EventProxy eventProxy;
	
	public EventQueue(EventProxy eventProxy) {
		this.eventProxy = eventProxy;
		addPage();
	}

	@Override
	public Event poll() {
		Long nextEventTime = eventProxy.nextEventTime();
		if (this.isEmpty() || (nextEventTime != null && nextEventTime <= this.peek().getTime())) {
			addPage();
		}
		return super.poll();
	}
	
	private void addPage() {
		this.addAll(eventProxy.nextEventPage(PAGE_SIZE));
	}
	
}

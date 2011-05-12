package br.edu.ufcg.lsd.oursim.queue;

import java.util.List;
import java.util.PriorityQueue;

import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.factories.EventFactory;

public class EventQueue extends PriorityQueue<Event>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6795297555525744590L;
	private static final int PAGE_SIZE = 100;
	
	private final EventProxy eventProxy;
	private final EventFactory eventFactory;
	
	public EventQueue(EventProxy eventProxy, EventFactory eventFactory) {
		this.eventProxy = eventProxy;
		this.eventFactory = eventFactory;
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
		List<EventSpec> nextEventPage = eventProxy.nextEventPage(PAGE_SIZE);
		for (EventSpec eventSpec : nextEventPage) {
			this.add(eventFactory.createEvent(eventSpec));
		}
	}
	
}

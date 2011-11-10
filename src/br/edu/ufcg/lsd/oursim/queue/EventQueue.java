package br.edu.ufcg.lsd.oursim.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.factories.EventFactory;

public class EventQueue {

	/**
	 * 
	 */
	private static final int PAGE_SIZE = 100;
	
	private final EventProxy eventProxy;
	private final EventFactory eventFactory;
	private final LinkedList<Event> queue = new LinkedList<Event>();
	
	public EventQueue(EventProxy eventProxy, EventFactory eventFactory) {
		this.eventProxy = eventProxy;
		this.eventFactory = eventFactory;
	}

	public Event poll() {
		Long nextEventTime = eventProxy.nextEventTime();
		if (queue.isEmpty() || (nextEventTime != null && nextEventTime <= head().getTime())) {
			addPage();
		}
		return removeHead();
	}
	
	private Event removeHead() {
		return queue.removeFirst();
	}
	
	public List<Event> getEvents() {
		return queue;
	}
	
	private Event head() {
		return queue.getFirst();
	}
	
	public boolean hasNext() {
		return !queue.isEmpty() || eventProxy.hasNextEvent();
	}
	
	private void addPage() {
		List<EventSpec> nextEventPage = eventProxy.nextEventPage(PAGE_SIZE);
		for (EventSpec eventSpec : nextEventPage) {
			this.add(eventFactory.createEvent(eventSpec));
		}
	}

	public void add(Event event) {
		
		ListIterator<Event> iterator = queue.listIterator();
		while (iterator.hasNext()) {
			Event itEvent = iterator.next();
			if (event.compareTo(itEvent) < 0) {
				iterator.previous();
				break;
			}
		}
		
		iterator.add(event);
	}

	public void clear() {
		queue.clear();
	}
	
}

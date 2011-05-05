package br.edu.ufcg.lsd.oursim.queue;

import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.events.Event;

public class ListEventProxy implements EventProxy {

	LinkedList<Event> events = new LinkedList<Event>();
	
	@Override
	public List<Event> nextEventPage(int pageSize) {
		int size = Math.min(pageSize, events.size());
		List<Event> removed = new LinkedList<Event>();
		for (int i = 0; i < size; i++) {
			removed.add(events.removeFirst());
		}
		return removed;
	}

	@Override
	public Long nextEventTime() {
		if (events.isEmpty()) {
			return null;
		}
		return events.getFirst().getTime();
	}

	public void add(Event ev) {
		events.add(ev);
	}

}

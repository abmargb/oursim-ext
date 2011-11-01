package br.edu.ufcg.lsd.oursim;

import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.queue.EventProxy;

public class ListEventProxy implements EventProxy {

	LinkedList<EventSpec> events = new LinkedList<EventSpec>();
	
	@Override
	public List<EventSpec> nextEventPage(int pageSize) {
		int size = Math.min(pageSize, events.size());
		List<EventSpec> removed = new LinkedList<EventSpec>();
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

	public void add(EventSpec ev) {
		events.add(ev);
	}

	@Override
	public boolean hasNextEvent() {
		return !events.isEmpty();
	}

}

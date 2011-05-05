package br.edu.ufcg.lsd.oursim.queue;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.factories.EventFactory;

public class DefaultEventProxy implements EventProxy {

	private Scanner scanner;
	private EventFactory eventFactory;
	private Event nextEvent;
	
	/**
	 * @param inputStream
	 */
	public DefaultEventProxy(InputStream inputStream) {
		this.scanner = new Scanner(inputStream);
		this.eventFactory = new EventFactory();
	}

	@Override
	public List<Event> nextEventPage(int pageSize) {
		
		List<Event> eventPage = new LinkedList<Event>();
		
		if (!scanner.hasNextLine()) {
			return eventPage;
		}
		
		if (nextEvent == null) {
			nextEvent = eventFactory.parseEvent(scanner.nextLine());
		}
		
		for (int i = 0; i < pageSize; i++) {
			eventPage.add(nextEvent);
			if (scanner.hasNextLine()) {
				nextEvent = eventFactory.parseEvent(scanner.nextLine());
			} else {
				nextEvent = null;
				break;
			}
		}
		
		return eventPage;
	}

	@Override
	public Long nextEventTime() {
		if (nextEvent == null) {
			return null;
		}
		return nextEvent.getTime();
	}

}

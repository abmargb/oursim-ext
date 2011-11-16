package br.edu.ufcg.lsd.oursim.queue;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.util.LineParser;

public class FileEventProxy implements EventProxy {

	private Scanner scanner;
	private EventSpec nextEvent;
	
	/**
	 * @param inputStream
	 */
	public FileEventProxy(InputStream inputStream) {
		this.scanner = new Scanner(inputStream);
		parseNextEvent();
	}

	private boolean parseNextEvent() {
		if (!scanner.hasNextLine()) {
			this.nextEvent = null;
			return false;
		}
		
		this.nextEvent = parseEvent(scanner.nextLine());
		return true;
	}

	@Override
	public List<EventSpec> nextEventPage(int pageSize) {
		
		List<EventSpec> eventPage = new LinkedList<EventSpec>();
		
		if (!scanner.hasNextLine()) {
			if (nextEvent != null) {
				eventPage.add(nextEvent);
			}
			return eventPage;
		}
		
		for (int i = 0; i < pageSize; i++) {
			eventPage.add(nextEvent);
			if (!parseNextEvent()) {
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
	
	private static EventSpec parseEvent(String line) {
		String[] split = line.split("\\s+");
		
		if (line.length() < 2) {
			throw new IllegalArgumentException(
					"Stream de eventos mal formatado.");
		}
		
		LineParser lineParser = new LineParser(line);
		
		String type = lineParser.next();
		Long time = Long.parseLong(lineParser.next());
		
		String data = split.length >= 3 ? lineParser.restOfLine() : null;
		
		return data == null ? new EventSpec(type, time)
				: new EventSpec(type, time, data);
	}

	@Override
	public boolean hasNextEvent() {
		return nextEvent != null;
	}
}

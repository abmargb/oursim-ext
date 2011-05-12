package br.edu.ufcg.lsd.oursim.factories;

import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.HaltEvent;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerDownEvent;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerUpEvent;
import br.edu.ufcg.lsd.oursim.events.broker.AddJobEvent;
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceDownEvent;
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceUpEvent;
import br.edu.ufcg.lsd.oursim.events.peer.PeerDownEvent;
import br.edu.ufcg.lsd.oursim.events.peer.PeerUpEvent;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerDownEvent;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerUpEvent;
import br.edu.ufcg.lsd.oursim.util.LineParser;

public class EventFactory {

	private Map<String, Class<?>> eventClasses = new HashMap<String, Class<?>>();
	
	public EventFactory() {
		eventClasses.put(DiscoveryServiceUpEvent.TYPE, DiscoveryServiceUpEvent.class);
		eventClasses.put(DiscoveryServiceDownEvent.TYPE, DiscoveryServiceDownEvent.class);
		eventClasses.put(PeerUpEvent.TYPE, PeerUpEvent.class);
		eventClasses.put(PeerDownEvent.TYPE, PeerDownEvent.class);
		eventClasses.put(WorkerUpEvent.TYPE, WorkerUpEvent.class);
		eventClasses.put(WorkerDownEvent.TYPE, WorkerDownEvent.class);
		eventClasses.put(BrokerUpEvent.TYPE, BrokerUpEvent.class);
		eventClasses.put(BrokerDownEvent.TYPE, BrokerDownEvent.class);
		eventClasses.put(HaltEvent.TYPE, HaltEvent.class);
		eventClasses.put(AddJobEvent.TYPE, AddJobEvent.class);
	}
	
	public Event parseEvent(String line) {
		String[] split = line.split("\\s+");
		
		if (line.length() < 2) {
			throw new IllegalArgumentException(
					"Stream de eventos mal formatado.");
		}
		
		LineParser lineParser = new LineParser(line);
		
		String type = lineParser.next();
		Long time = Long.parseLong(lineParser.next());
		
		String data = split.length >= 3 ? lineParser.restOfLine() : null;
		
		Class<?> eventClass = eventClasses.get(type);
		
		try {
			return (Event) eventClass.getConstructor(
					Long.class, String.class).newInstance(
							time, data);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} 
	}
}

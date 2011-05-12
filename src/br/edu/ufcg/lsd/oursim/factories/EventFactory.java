package br.edu.ufcg.lsd.oursim.factories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.HaltEvent;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceEvents;
import br.edu.ufcg.lsd.oursim.events.fd.FailureDetectionEvents;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;

public class EventFactory {

	private Map<String, Class<?>> eventClasses = new HashMap<String, Class<?>>();
	
	public EventFactory() {
		eventClasses.putAll(BrokerEvents.createEvents());
		eventClasses.putAll(DiscoveryServiceEvents.createEvents());
		eventClasses.putAll(FailureDetectionEvents.createEvents());
		eventClasses.putAll(PeerEvents.createEvents());
		eventClasses.putAll(WorkerEvents.createEvents());
		
		eventClasses.put(HaltEvent.TYPE, HaltEvent.class);
	}
	
	
	public Event createEvent(String type, long time, Object... params) {
		Class<?> eventClass = eventClasses.get(type);
		
		Class<?>[] paramsClasses = new Class<?>[params.length + 1];
		paramsClasses[0] = Long.class;
		
		for (int i = 0; i < params.length; i++) {
			paramsClasses[i+1] = params[i].getClass();
		}
		
		Object[] initArgs = new Object[params.length + 1];
		initArgs[0] = time;
		
		for (int i = 0; i < params.length; i++) {
			initArgs[i+1] = params[i];
		}
		
		try {
			return (Event) eventClass.getConstructor(paramsClasses).newInstance(initArgs);
		} catch (Exception e) {
			throw new IllegalArgumentException("Event not found for type: [" + type + "] " +
					"and parameter types: " + Arrays.asList(paramsClasses).toString(), e);
		}
	}
	
	public Event createEvent(EventSpec eventSpec) {
		return createEvent(eventSpec.getType(), eventSpec.getTime(), eventSpec.getParams());
	}
	
}

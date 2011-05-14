package br.edu.ufcg.lsd.oursim.events.ds;

import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.events.Event;

public class DiscoveryServiceEvents {

	
	/**
	 * Primary Events
	 */
	public static final String DS_DOWN = "DS_DOWN";
	public static final String DS_UP = "DS_UP";
	
	/**
	 * Secondary Events 
	 */
	public static final String GET_WORKER_PROVIDERS = "GET_WORKER_PROVIDERS";
	
	
	public static Map<String, Class<? extends Event>> createEvents() {
		Map<String, Class<? extends Event>> events = new HashMap<String, Class<? extends Event>>();
		events.put(DS_DOWN, DiscoveryServiceDownEvent.class);
		events.put(DS_UP, DiscoveryServiceUpEvent.class);
		events.put(GET_WORKER_PROVIDERS, GetWorkerProvidersEvent.class);
		
		return events;
	}
	
}

package br.edu.ufcg.lsd.oursim.events.worker;

import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.events.Event;

public class WorkerEvents {

	
	/**
	 * Primary Events
	 */
	public static final String WORKER_DOWN = "WORKER_DOWN";
	public static final String WORKER_UP = "WORKER_UP";
	
	
	/**
	 * Secondary Events
	 */
	public static final String START_WORK = "START_WORK";
	
	public static Map<String, Class<? extends Event>> createEvents() {
		Map<String, Class<? extends Event>> events = new HashMap<String, Class<? extends Event>>();
		events.put(WORKER_DOWN, WorkerDownEvent.class);
		events.put(WORKER_UP, WorkerUpEvent.class);
		events.put(START_WORK, StartWorkEvent.class);
		
		return events;
	}
	
}

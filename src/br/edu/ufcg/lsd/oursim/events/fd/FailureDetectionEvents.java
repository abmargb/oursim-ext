package br.edu.ufcg.lsd.oursim.events.fd;

import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.events.Event;

public class FailureDetectionEvents {

	/**
	 * Secondary Events
	 */
	public static final String ACTIVE_ENTITY_DOWN = "ACTIVE_ENTITY_DOWN";
	public static final String ACTIVE_ENTITY_UP = "ACTIVE_ENTITY_UP";
	public static final String IS_IT_ALIVE_RECEIVED = "IS_IT_ALIVE_RECEIVED";
	public static final String IS_IT_ALIVE_SENT = "IS_IT_ALIVE_SENT";
	public static final String LIVENESS_CHECK = "LIVENESS_CHECK";
	public static final String UPDATE_STATUS_AVAILABLE = "UPDATE_STATUS_AVAILABLE";
	public static final String RELEASE = "RELEASE";
	
	public static Map<String, Class<? extends Event>> createEvents() {
		Map<String, Class<? extends Event>> events = new HashMap<String, Class<? extends Event>>();
		events.put(ACTIVE_ENTITY_DOWN, ActiveEntityDownEvent.class);
		events.put(ACTIVE_ENTITY_UP, ActiveEntityUpEvent.class);
		events.put(IS_IT_ALIVE_RECEIVED, IsItAliveReceivedEvent.class);
		events.put(IS_IT_ALIVE_SENT, IsItAliveSentEvent.class);
		events.put(LIVENESS_CHECK, LivenessCheckEvent.class);
		events.put(UPDATE_STATUS_AVAILABLE, UpdateStatusAvailableEvent.class);
		events.put(RELEASE, ReleaseMonitoredEvent.class);
		
		return events;
	}
	
}

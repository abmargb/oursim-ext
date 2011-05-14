package br.edu.ufcg.lsd.oursim.events.peer;

import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.events.Event;

public class PeerEvents {

	
	/**
	 * Primary Events
	 */
	public static final String PEER_DOWN = "PEER_DOWN";
	public static final String PEER_UP = "PEER_UP";
	
	
	/**
	 * Secondary Events
	 */
	public static final String BROKER_LOGIN = "BROKER_LOGIN";
	public static final String DISCOVERY_SERVICE_AVAILABLE = "DISCOVERY_SERVICE_AVAILABLE";
	public static final String DISCOVERY_SERVICE_FAILED = "DISCOVERY_SERVICE_FAILED";
	public static final String DISPOSE_WORKER = "DISPOSE_WORKER";
	public static final String FINISH_REQUEST = "FINISH_REQUEST";
	public static final String PAUSE_REQUEST = "PAUSE_REQUEST";
	public static final String REQUEST_WORKERS = "REQUEST_WORKERS";
	public static final String RESUME_REQUEST = "RESUME_REQUEST";
	public static final String WORKER_IDLE = "WORKER_IDLE";
	public static final String WORKER_UNAVAILABLE = "WORKER_UNAVAILABLE";
	public static final String WORKER_ALLOCATED = "WORKER_ALLOCATED";
	
	public static Map<String, Class<? extends Event>> createEvents() {
		Map<String, Class<? extends Event>> events = new HashMap<String, Class<? extends Event>>();
		events.put(PEER_DOWN, PeerDownEvent.class);
		events.put(PEER_UP, PeerUpEvent.class);
		events.put(BROKER_LOGIN, BrokerLoginEvent.class);
		events.put(DISCOVERY_SERVICE_AVAILABLE, DiscoveryServiceAvailableEvent.class);
		events.put(DISCOVERY_SERVICE_FAILED, DiscoveryServiceFailedEvent.class);
		events.put(DISPOSE_WORKER, DisposeWorkerEvent.class);
		events.put(FINISH_REQUEST, FinishRequestEvent.class);
		events.put(PAUSE_REQUEST, PauseRequestEvent.class);
		events.put(REQUEST_WORKERS, RequestWorkersEvent.class);
		events.put(RESUME_REQUEST, ResumeRequestEvent.class);
		events.put(WORKER_IDLE, WorkerIdleEvent.class);
		events.put(WORKER_UNAVAILABLE, WorkerUnavailableEvent.class);
		events.put(WORKER_ALLOCATED, WorkerAllocatedEvent.class);
		
		return events;
	}
	
}

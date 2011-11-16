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
	public static final String DISCOVERY_SERVICE_FAILED = "DISCOVERY_SERVICE_FAILED";
	public static final String DISPOSE_WORKER = "DISPOSE_WORKER";
	public static final String FINISH_REQUEST = "FINISH_REQUEST";
	public static final String PAUSE_REQUEST = "PAUSE_REQUEST";
	public static final String REQUEST_WORKERS = "REQUEST_WORKERS";
	public static final String RESUME_REQUEST = "RESUME_REQUEST";
	public static final String WORKER_IDLE = "WORKER_IDLE";
	public static final String LOCAL_WORKER_FAILED = "LOCAL_WORKER_FAILED";
	public static final String LOCAL_WORKER_AVAILABLE = "LOCAL_WORKER_AVAILABLE";
	public static final String WORKER_FAILED = "WORKER_FAILED_FOR_PEER";
	public static final String WORKER_AVAILABLE = "WORKER_AVAILABLE_FOR_PEER";
	public static final String WORKER_IN_USE = "WORKER_IN_USE";
	public static final String HERE_ARE_WORKER_PROVIDERS = "HERE_ARE_WORKER_PROVIDERS";
	public static final String REPEAT_GET_WORKER_PROVIDERS = "REPEAT_GET_WORKER_PROVIDERS";
	public static final String REMOTE_REQUEST_WORKERS = "REMOTE_REQUEST_WORKERS";
	public static final String WORKER_DONATED = "WORKER_DONATED";
	public static final String REMOTE_HERE_IS_WORKER = "REMOTE_HERE_IS_WORKER";
	public static final String REMOTE_WORKER_AVAILABLE = "REMOTE_WORKER_AVAILABLE";
	public static final String REMOTE_WORKER_FAILED = "REMOTE_WORKER_FAILED";
	public static final String DISPOSE_REMOTE_WORKER = "DISPOSE_REMOTE_WORKER";
	public static final String REPORT_REPLICA_ACCOUNTING = "REPORT_REPLICA_ACCOUNTING";
	public static final String REPORT_WORK_ACCOUNTING = "REPORT_WORK_ACCOUNTING";
	public static final String BROKER_AVAILABLE = "BROKER_AVAILABLE_FOR_PEER";
	public static final String BROKER_FAILED = "BROKER_FAILED_FOR_PEER";
	
	public static Map<String, Class<? extends Event>> createEvents() {
		Map<String, Class<? extends Event>> events = new HashMap<String, Class<? extends Event>>();
		events.put(PEER_DOWN, PeerDownEvent.class);
		events.put(PEER_UP, PeerUpEvent.class);
		events.put(BROKER_LOGIN, BrokerLoginEvent.class);
		events.put(DISCOVERY_SERVICE_FAILED, DiscoveryServiceFailedEvent.class);
		events.put(DISPOSE_WORKER, DisposeWorkerEvent.class);
		events.put(FINISH_REQUEST, FinishRequestEvent.class);
		events.put(PAUSE_REQUEST, PauseRequestEvent.class);
		events.put(REQUEST_WORKERS, RequestWorkersEvent.class);
		events.put(RESUME_REQUEST, ResumeRequestEvent.class);
		events.put(WORKER_IDLE, WorkerIdleEvent.class);
		events.put(LOCAL_WORKER_FAILED, LocalWorkerFailedEvent.class);
		events.put(LOCAL_WORKER_AVAILABLE, LocalWorkerAvailableEvent.class);
		events.put(WORKER_FAILED, WorkerFailedEvent.class);
		events.put(WORKER_AVAILABLE, WorkerAvailableEvent.class);
		events.put(WORKER_IN_USE, WorkerInUseEvent.class);
		events.put(HERE_ARE_WORKER_PROVIDERS, HereAreWorkerProvidersEvent.class);
		events.put(REPEAT_GET_WORKER_PROVIDERS, RepeatGetWorkerProvidersEvent.class);
		events.put(REMOTE_REQUEST_WORKERS, RemoteRequestWorkersEvent.class);
		events.put(WORKER_DONATED, WorkerDonatedEvent.class);
		events.put(REMOTE_HERE_IS_WORKER, HereIsRemoteWorkerEvent.class);
		events.put(REMOTE_WORKER_AVAILABLE, RemoteWorkerAvailableEvent.class);
		events.put(REMOTE_WORKER_FAILED, RemoteWorkerFailedEvent.class);
		events.put(DISPOSE_REMOTE_WORKER, DisposeRemoteWorkerEvent.class);
		events.put(REPORT_REPLICA_ACCOUNTING, ReportReplicaAccountingEvent.class);
		events.put(REPORT_WORK_ACCOUNTING, ReportWorkAccountingEvent.class);
		events.put(BROKER_AVAILABLE, BrokerAvailableEvent.class);
		events.put(BROKER_FAILED, BrokerFailedEvent.class);
		
		return events;
	}
	
}

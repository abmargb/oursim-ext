package br.edu.ufcg.lsd.oursim.acceptance;

import java.util.List;
import java.util.Properties;

import br.edu.ufcg.lsd.oursim.ListEventProxy;
import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.DiscoveryService;
import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.global.HaltEvent;
import br.edu.ufcg.lsd.oursim.network.BlankNetwork;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class AcceptanceTest {

	private ListEventProxy eventProxy = new ListEventProxy();
	private Grid grid = new Grid();
	private Properties properties = Configuration.createConfiguration(new Properties());
	private OurSim ourSim;
	private EventRecorder recorder = new EventRecorder();
	
	public AcceptanceTest() {
		initProperties();
	}

	private void initProperties() {
		properties.put(Configuration.PROP_BROKER_MAX_REPLICAS, "1");
		properties.put(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.FALSE.toString());
		properties.put(Configuration.PROP_BROKER_SCHEDULER_INTERVAL, "0");
	}
	
	private List<Event> addEvent(EventSpec evSpec, boolean halt) {
		recorder.startRecording();
		eventProxy.add(evSpec);

		if (halt) {
			eventProxy.add(new EventSpec(HaltEvent.TYPE, evSpec.getTime() + 1));
		}

		step();
		List<Event> secondary = recorder.stopRecording();
		return secondary.subList(1, secondary.size() - (halt ? 1 : 0));
	}
	
	protected List<Event> addEvent(EventSpec evSpec) {
		return addEvent(evSpec, false);
	}
	
	/**
	 * Intended for events that generate infinite secondary events.
	 * @param evSpec
	 * @return 
	 */
	protected List<Event> addEventAndHalt(EventSpec evSpec) {
		return addEvent(evSpec, true);
	}
	
	
	protected Broker createBroker(String brokerId) {
		return (Broker) addActiveEntity(
				brokerId, new Broker());
	}
	
	protected Worker createWorker(String workerId) {
		Worker worker = (Worker) addActiveEntity(
				workerId, new Worker());
		worker.setCpu(1.);
		return worker;
	}
	
	protected DiscoveryService createDiscoveryService(String dsId) {
		return (DiscoveryService) addActiveEntity(
				dsId, new DiscoveryService());
	}
	
	protected Peer createPeer(String peerId) {
		return (Peer) addActiveEntity(
				peerId, new Peer());
	}

	private ActiveEntity addActiveEntity(String entityId, 
			ActiveEntity entity) {
		entity.setId(entityId);
		grid.addObject(entity);
		return entity;
	}
	
	private void step() {
		if (ourSim == null) {
			this.ourSim = new OurSim(
					eventProxy, 
					grid,
					properties,
					new BlankNetwork(),
					new TestTraceCollector());
			this.ourSim.addEventListener(recorder);
		}
		
		ourSim.run();
	}
	
}

package br.edu.ufcg.lsd.oursim.acceptance;

import java.util.List;
import java.util.Properties;

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
import br.edu.ufcg.lsd.oursim.queue.ListEventProxy;
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
	
	public List<Event> addEvent(EventSpec evSpec) {
		return addEvent(evSpec, null);
	}
	
	public List<Event> addEventAndReturn(EventSpec evSpec) {
		HaltByEventCondition haltAfter = haltAfter(evSpec.getType());
		
		eventProxy.add(evSpec);
		eventProxy.add(new EventSpec(HaltEvent.TYPE, evSpec.getTime() + 1));
		step();
		
		getSim().removeEventListener(haltAfter);
		
		return getSim().getQueue().getEvents();
	}
	
	public List<Event> addEvent(EventSpec evSpec, String haltAfterType) {
		recorder.startRecording();
		
		HaltByEventCondition haltAfter = haltAfter(haltAfterType);
		
		eventProxy.add(evSpec);
		eventProxy.add(new EventSpec(HaltEvent.TYPE, evSpec.getTime() + 1));
		step();
		
		getSim().removeEventListener(haltAfter);
		
		List<Event> secondary = recorder.stopRecording();
		return secondary.subList(1, secondary.size() - (haltAfterType != null ? 0 : 1));
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
	
	protected void setProperty(String property, String value) {
		properties.put(property, value);
	}
	
	private void step() {
		getSim().run();
	}

	private OurSim getSim() {
		if (ourSim == null) {
			this.ourSim = new OurSim(
					eventProxy, 
					grid,
					properties,
					new BlankNetwork(),
					new TestTraceCollector());
			this.ourSim.addEventListener(recorder);
		}
		return this.ourSim;
	}
	
	private HaltByEventCondition haltAfter(String eventType) {
		
		if (eventType == null) {
			return null;
		}
		
		HaltByEventCondition evListener = new HaltByEventCondition(
				getSim(), eventType);
		getSim().addEventListener(evListener);
		return evListener;
	}
	
}

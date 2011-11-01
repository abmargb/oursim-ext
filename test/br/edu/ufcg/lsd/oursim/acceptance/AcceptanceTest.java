package br.edu.ufcg.lsd.oursim.acceptance;

import java.util.Properties;

import br.edu.ufcg.lsd.oursim.ListEventProxy;
import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.DiscoveryService;
import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.network.BlankNetwork;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class AcceptanceTest {

	private ListEventProxy eventProxy = new ListEventProxy();
	private Grid grid = new Grid();
	private Properties properties = new Properties();
	private OurSim ourSim;
	
	public AcceptanceTest() {
		initProperties();
	}

	private void initProperties() {
		properties.put(Configuration.PROP_BROKER_MAX_REPLICAS, "1");
		properties.put(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.FALSE.toString());
		properties.put(Configuration.PROP_BROKER_SCHEDULER_INTERVAL, "0");
	}
	
	protected void addEvent(EventSpec evSpec) {
		eventProxy.add(evSpec);
		step();
	}
	
	protected Broker createBroker(String brokerId) {
		return (Broker) addActiveEntity(
				brokerId, new Broker());
	}
	
	protected Worker createWorker(String workerId) {
		return (Worker) addActiveEntity(
				workerId, new Worker());
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
					null);
		}
		
		ourSim.run();
	}
}

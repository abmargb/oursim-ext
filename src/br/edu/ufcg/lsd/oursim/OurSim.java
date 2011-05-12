package br.edu.ufcg.lsd.oursim;

import java.util.Properties;

import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.factories.GridFactory;
import br.edu.ufcg.lsd.oursim.network.Network;
import br.edu.ufcg.lsd.oursim.queue.EventProxy;
import br.edu.ufcg.lsd.oursim.queue.EventQueue;
import br.edu.ufcg.lsd.oursim.trace.TraceCollector;
import br.edu.ufcg.lsd.oursim.util.Configuration;

/**
 *
 */
public class OurSim {

	private final EventQueue queue;
	private final Grid grid;
	private final Network network;
	private final Properties properties;
	private final TraceCollector traceCollector;
	
	private boolean running = true;
	
	public OurSim(EventProxy eventProxy, GridFactory gridFactory, 
			Properties properties, Network network, TraceCollector traceCollector) {
		this.traceCollector = traceCollector;
		this.properties = createProperties(properties);
		this.network = network;
		this.queue = new EventQueue(eventProxy);
		this.grid = gridFactory.createGrid();
	}
	
	private Properties createProperties(Properties properties) {
		Properties defaults = new Properties(Configuration.createDefault());
		defaults.putAll(properties);
		return defaults;
	}

	public Grid getGrid() {
		return grid;
	}

	public void addEvent(Event event) {
		queue.add(event);
	}
	
	public void addNetworkEvent(Event event) {
		event.setTime(event.getTime() + network.generateDelay());
		queue.add(event);
	}
	
	public void run() {
		while (!queue.isEmpty() && running) {
			Event ev = queue.poll();
			ev.process(this);
		}
	}
	
	public Long getLongProperty(String key) {
		String property = properties.getProperty(key);
		return property == null ? null : Long.valueOf(property);
	}
	
	public Integer getIntProperty(String key) {
		String property = properties.getProperty(key);
		return property == null ? null : Integer.valueOf(property);
	}
	
	public Boolean getBooleanProperty(String key) {
		String property = properties.getProperty(key);
		return property == null ? null : Boolean.valueOf(property);
	}
	
	public String getStringProperty(String key) {
		return properties.getProperty(key);
	}

	public TraceCollector getTraceCollector() {
		return traceCollector;
	}
	
	public void halt() {
		this.running = false;
	}
}

package br.edu.ufcg.lsd.oursim;

import java.util.Properties;

import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.factories.GridFactory;
import br.edu.ufcg.lsd.oursim.network.Network;
import br.edu.ufcg.lsd.oursim.queue.EventProxy;
import br.edu.ufcg.lsd.oursim.queue.EventQueue;
import br.edu.ufcg.lsd.oursim.util.Configuration;

/**
 *
 */
public class OurSim {

	private final EventQueue queue;
	private final Grid grid;
	private final Network network;
	private final Properties properties;
	private boolean running = true;
	
	public OurSim(EventProxy eventProxy, GridFactory gridFactory, 
			Properties properties, Network network) {
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

	public OurSim(EventProxy eventProxy, GridFactory gridFactory, 
			Properties properties) {
		this(eventProxy, gridFactory, properties, null);
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
	
	public Boolean getBooleanProperty(String key) {
		String property = properties.getProperty(key);
		return property == null ? null : Boolean.valueOf(property);
	}
	
	public String getStringProperty(String key) {
		return properties.getProperty(key);
	}

	public void halt() {
		this.running = false;
	}
}

package br.edu.ufcg.lsd.oursim;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventListener;
import br.edu.ufcg.lsd.oursim.factories.EventFactory;
import br.edu.ufcg.lsd.oursim.network.Network;
import br.edu.ufcg.lsd.oursim.queue.EventProxy;
import br.edu.ufcg.lsd.oursim.queue.EventQueue;
import br.edu.ufcg.lsd.oursim.trace.TraceCollector;

/**
 *
 */
public class OurSim {

	private static final int SEED = 123455677;
	
	private final EventQueue queue;
	private final Grid grid;
	private final Network network;
	private final Properties properties;
	private final TraceCollector traceCollector;
	private final Random random = new Random(SEED);
	private final List<EventListener> eventListeners = new LinkedList<EventListener>();
	
	private boolean running = true;
	private boolean livenessCheckScheduled = false;
	private final EventFactory eventFactory = new EventFactory();
	
	public OurSim(EventProxy eventProxy, Grid grid, 
			Properties properties, Network network, TraceCollector traceCollector) {
		this.traceCollector = traceCollector;
		this.properties = properties;
		this.network = network;
		this.queue = new EventQueue(eventProxy, eventFactory);
		this.grid = grid;
	}
	
	public EventFactory getEventFactory() {
		return eventFactory;
	}
	
	public void addEventListener(EventListener evListener) {
		eventListeners.add(evListener);
	}
	
	public Random getRandom() {
		return random;
	}
	
	public Grid getGrid() {
		return grid;
	}

	public void addEvent(Event event) {
		queue.add(event);
	}
	
	public void addNetworkEvent(Event event) {
		long delayedTime = event.getTime() + network.generateDelay();
		event.setTime(delayedTime);
		queue.add(event);
	}
	
	public Event createEvent(String type, long time, Object... params) {
		return eventFactory.createEvent(type, time, params);
	}
	
	public void run() {
		this.running = true;
		this.queue.clear();
		
		while (queue.hasNext() && running) {
			Event ev = queue.poll();
			ev.process(this);
			notifyEventListeners(ev);
		}
	}
	
	private void notifyEventListeners(Event ev) {
		for (EventListener eventListener : eventListeners) {
			eventListener.eventProcessed(ev);
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

	public void removeEventListener(EventListener eventListener) {
		if (eventListener != null) {
			eventListeners.remove(eventListener);
		}
	}
	
	public EventQueue getQueue() {
		return queue;
	}

	public boolean isLivenessCheckSchedule() {
		return livenessCheckScheduled;
	}

	public void setLivenessCheckSchedule(boolean livenessCheckSchedule) {
		this.livenessCheckScheduled = livenessCheckSchedule;
	}
}

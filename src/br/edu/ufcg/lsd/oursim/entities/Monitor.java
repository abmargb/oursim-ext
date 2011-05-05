package br.edu.ufcg.lsd.oursim.entities;

import br.edu.ufcg.lsd.oursim.events.Event;


public class Monitor {

	private ActiveEntity object;
	private boolean isUp;
	private final Event callbackAliveEvent;
	private final Event callbackDownEvent;
	
	public Monitor(ActiveEntity monitoredObj, 
			Event callbackAliveEvent, Event callbackDownEvent) {
		object = monitoredObj;
		this.callbackAliveEvent = callbackAliveEvent;
		this.callbackDownEvent = callbackDownEvent;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}
	
	public boolean isUp() {
		return isUp;
	}

	public void setObject(ActiveEntity object) {
		this.object = object;
	}

	public ActiveEntity getObject() {
		return object;
	}

	public Event getCallbackAliveEvent() {
		return callbackAliveEvent;
	}

	public Event getCallbackDownEvent() {
		return callbackDownEvent;
	}
	
}

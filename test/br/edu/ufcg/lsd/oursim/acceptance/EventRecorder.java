package br.edu.ufcg.lsd.oursim.acceptance;

import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventListener;

public class EventRecorder implements EventListener {

	private final List<Event> recordedEvents = new LinkedList<Event>();
	private boolean recording = false;

	@Override
	public void eventProcessed(Event e) {
		if (recording) {
			recordedEvents.add(e);
		}
	}

	public void startRecording() {
		recording = true;
	}
	
	public List<Event> stopRecording() {
		recording = false;
		List<Event> recentlyRecorded = new LinkedList<Event>(recordedEvents);
		recordedEvents.clear();
		
		return recentlyRecorded;
	}
}

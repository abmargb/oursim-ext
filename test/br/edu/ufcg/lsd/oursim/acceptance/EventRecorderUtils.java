package br.edu.ufcg.lsd.oursim.acceptance;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.events.Event;

public class EventRecorderUtils {

	public static boolean hasEvent(List<Event> events, String type) {
		for (Event event : events) {
			if (type.equals(event.getType())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasEventSequence(List<Event> events, String... types) {
		
		List<String> expectedTypes = Arrays.asList(types);
		List<String> actualTypes = new LinkedList<String>();
		
		for (Event event : events) {
			actualTypes.add(event.getType());
		}
		
		return Collections.indexOfSubList(actualTypes, expectedTypes) >= 0;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Event> T getEvent(List<Event> events, String type) {
		
		for (Event event : events) {
			if (type.equals(event.getType())) {
				return (T) event;
			}
		}
		return null;
	}
	
}

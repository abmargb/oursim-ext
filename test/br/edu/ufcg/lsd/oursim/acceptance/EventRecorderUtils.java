package br.edu.ufcg.lsd.oursim.acceptance;

import java.util.List;

import br.edu.ufcg.lsd.oursim.events.Event;

public class EventRecorderUtils {

	public static boolean hasEventOfType(List<Event> events, String type) {
		for (Event event : events) {
			if (type.equals(event.getType())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasEventSequence(List<Event> events, String... types) {
		
		boolean comparing = false;
		int i = 0;
		for (Event event : events) {
			if (!event.getType().equals(types[i])) {
				if (comparing) {
					return false;
				}
			} else {
				comparing = true;
			}
			if (comparing) {
				i++;
			}
		}
		return true;
	}
	
}

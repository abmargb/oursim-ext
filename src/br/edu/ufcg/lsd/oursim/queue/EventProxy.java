package br.edu.ufcg.lsd.oursim.queue;

import java.util.List;

import br.edu.ufcg.lsd.oursim.events.Event;

public interface EventProxy {

	public List<Event> nextEventPage(int pageSize);
	
	public Long nextEventTime();
	
}
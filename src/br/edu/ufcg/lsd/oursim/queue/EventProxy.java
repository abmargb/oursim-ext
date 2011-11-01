package br.edu.ufcg.lsd.oursim.queue;

import java.util.List;

import br.edu.ufcg.lsd.oursim.events.EventSpec;

public interface EventProxy {

	public List<EventSpec> nextEventPage(int pageSize);
	
	public Long nextEventTime();
	
	public boolean hasNextEvent();
	
}
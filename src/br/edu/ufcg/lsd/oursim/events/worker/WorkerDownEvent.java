package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityDownEvent;

public class WorkerDownEvent extends ActiveEntityDownEvent {

	public static final String TYPE = "WORKER_DOWN";
	
	public WorkerDownEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}

}

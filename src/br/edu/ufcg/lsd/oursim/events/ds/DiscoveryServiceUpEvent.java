package br.edu.ufcg.lsd.oursim.events.ds;

import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;

public class DiscoveryServiceUpEvent extends ActiveEntityUpEvent {

	public static final String TYPE = "DS_UP";

	public DiscoveryServiceUpEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}

}

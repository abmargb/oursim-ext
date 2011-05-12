package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityDownEvent;

public class PeerDownEvent extends ActiveEntityDownEvent {

	public PeerDownEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}

	@Override
	protected void entityDown(OurSim ourSim) {
		
	}

}

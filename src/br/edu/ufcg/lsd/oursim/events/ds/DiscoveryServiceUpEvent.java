package br.edu.ufcg.lsd.oursim.events.ds;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;

public class DiscoveryServiceUpEvent extends ActiveEntityUpEvent {

	public DiscoveryServiceUpEvent(String data) {
		super(Event.DEF_PRIORITY, data);
	}

	@Override
	protected void entityUp(OurSim ourSim) {
		
	}

}

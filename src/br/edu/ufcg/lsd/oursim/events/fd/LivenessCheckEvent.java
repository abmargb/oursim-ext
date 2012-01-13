package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class LivenessCheckEvent extends AbstractEvent {

	public LivenessCheckEvent() {
		super(Event.DEF_PRIORITY);
	}

	@Override
	public void process(OurSim ourSim) {
		MonitorUtil.checkLiveness(ourSim, getTime());
	}

}

package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.PrimaryEvent;

public abstract class ActiveEntityUpEvent extends PrimaryEvent {

	public ActiveEntityUpEvent(Integer priority, String data) {
		super(priority, data);
	}

	@Override
	public final void process(OurSim ourSim) {
		MonitorUtil.objectIsUp(ourSim, getData(), getTime());
		entityUp(ourSim);
	}

	protected abstract void entityUp(OurSim ourSim);

}

package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.PrimaryEvent;

public abstract class ActiveEntityDownEvent extends PrimaryEvent {

	public ActiveEntityDownEvent(Integer priority, String data) {
		super(priority, data);
	}

	@Override
	public final void process(OurSim ourSim) {
		MonitorUtil.objectIsDown(ourSim, getData(), getTime());
		entityDown(ourSim);
	}

	protected abstract void entityDown(OurSim ourSim);

}

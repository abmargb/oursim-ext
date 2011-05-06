package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;

public abstract class ActiveEntityDownEvent extends AbstractEvent {

	public ActiveEntityDownEvent(Long time, Integer priority, String data) {
		super(time, priority, data);
	}

	@Override
	public final void process(OurSim ourSim) {
		ActiveEntity object = ourSim.getGrid().getObject(getData());
		object.setUp(false);
		MonitorUtil.objectIsDown(ourSim, object, getTime());
		entityDown(ourSim);
	}

	protected abstract void entityDown(OurSim ourSim);

}

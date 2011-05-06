package br.edu.ufcg.lsd.oursim.events.fd;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;

public abstract class ActiveEntityUpEvent extends AbstractEvent {

	public ActiveEntityUpEvent(Long time, Integer priority, String data) {
		super(time, priority, data);
	}

	@Override
	public final void process(OurSim ourSim) {
		ActiveEntity object = ourSim.getGrid().getObject(getData());
		object.setUp(true);
		MonitorUtil.objectIsUp(ourSim, object, getTime());
		entityUp(ourSim);
	}

	protected abstract void entityUp(OurSim ourSim);

}

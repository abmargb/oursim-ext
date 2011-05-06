package br.edu.ufcg.lsd.oursim.events.worker;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.fd.ActiveEntityUpEvent;

public class WorkerUpEvent extends ActiveEntityUpEvent {

	public static final String TYPE = "WORKER_UP";
	
	public WorkerUpEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}

	@Override
	protected void entityUp(OurSim ourSim) {
		// TODO Auto-generated method stub
		
	}

}

package br.edu.ufcg.lsd.oursim.entities.job;

import br.edu.ufcg.lsd.oursim.entities.Entity;

public class Replica extends Entity {

	private ExecutionState state = ExecutionState.UNSTARTED;
	
	public ExecutionState getState() {
		return state;
	}
	
}

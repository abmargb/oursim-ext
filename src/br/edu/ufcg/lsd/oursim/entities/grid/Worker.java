package br.edu.ufcg.lsd.oursim.entities.grid;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;

public class Worker extends ActiveEntity {

	private String consumer;

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public String getConsumer() {
		return consumer;
	}
	
}

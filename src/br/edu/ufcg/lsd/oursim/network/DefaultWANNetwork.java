package br.edu.ufcg.lsd.oursim.network;

import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.rng.BasicRandomStreamFactory;
import umontreal.iro.lecuyer.rng.MRG31k3p;


public class DefaultWANNetwork implements Network {

	private static final Double AVG_DELAY = 1/50d;
	private final ExponentialGen random;

	public DefaultWANNetwork(Double avgDelay) {
		this.random = new ExponentialGen(
				new BasicRandomStreamFactory(MRG31k3p.class).newInstance(), avgDelay);
	}
	
	public DefaultWANNetwork() {
		this(AVG_DELAY);
	}
	
	@Override
	public Long generateDelay() {
		return (long) random.nextDouble();
	}
	
}

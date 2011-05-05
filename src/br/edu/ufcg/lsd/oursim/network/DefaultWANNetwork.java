package br.edu.ufcg.lsd.oursim.network;

import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.rng.BasicRandomStreamFactory;
import umontreal.iro.lecuyer.rng.MRG31k3p;


public class DefaultWANNetwork implements Network {

	private static final Double AVG_DELAY = 1/500d;
	
	private ExponentialGen random = new ExponentialGen(
			new BasicRandomStreamFactory(MRG31k3p.class).newInstance(), AVG_DELAY);

	@Override
	public Long generateDelay() {
		return (long) random.nextDouble();
	}
	
}

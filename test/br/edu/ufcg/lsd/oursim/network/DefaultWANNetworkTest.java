package br.edu.ufcg.lsd.oursim.network;

import org.junit.Assert;
import org.junit.Test;

public class DefaultWANNetworkTest {

	@Test
	public void testDefaultWANSeed() {
		// 31k3 seed
		DefaultWANNetwork network = new DefaultWANNetwork();

		Assert.assertEquals(66, network.generateDelay().longValue());
		Assert.assertEquals(47, network.generateDelay().longValue());
		Assert.assertEquals(5, network.generateDelay().longValue());
		Assert.assertEquals(52, network.generateDelay().longValue());
		Assert.assertEquals(22, network.generateDelay().longValue());
		
	}

	@Test
	public void testWANWithParameters() {
		// 31k3 seed
		DefaultWANNetwork network = new DefaultWANNetwork(1/500d);

		Assert.assertEquals(364, network.generateDelay().longValue());
		Assert.assertEquals(131, network.generateDelay().longValue());
		Assert.assertEquals(224, network.generateDelay().longValue());
		Assert.assertEquals(58, network.generateDelay().longValue());
		Assert.assertEquals(348, network.generateDelay().longValue());
		
	}
	
}

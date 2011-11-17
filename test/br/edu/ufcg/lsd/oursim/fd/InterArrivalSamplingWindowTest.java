package br.edu.ufcg.lsd.oursim.fd;

import junit.framework.Assert;

import org.junit.Test;

public class InterArrivalSamplingWindowTest {

	@Test
	public void testScrolling() {
		InterArrivalSamplingWindow window = new InterArrivalSamplingWindow(2);
		
		window.addInterArrival(10L);
		Assert.assertEquals(10., window.getMean());
		Assert.assertEquals(0., window.getStandardDeviation());
		
		window.addInterArrival(20L);
		Assert.assertEquals(15., window.getMean());
		Assert.assertEquals(5., window.getStandardDeviation());
		
		window.addInterArrival(30L);
		Assert.assertEquals(25., window.getMean());
		Assert.assertEquals(5., window.getStandardDeviation());
		
		window.clear();
		Assert.assertEquals(0., window.getMean());
		Assert.assertEquals(0., window.getStandardDeviation());
	}
	
}

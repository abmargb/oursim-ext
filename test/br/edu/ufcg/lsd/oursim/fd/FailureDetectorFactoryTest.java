package br.edu.ufcg.lsd.oursim.fd;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

public class FailureDetectorFactoryTest {

	@Test(expected=IllegalArgumentException.class)
	public void testFactoryInavalidDetector() throws Exception {
		FailureDetectorFactory factory = new FailureDetectorFactory();
		
		factory.createFd("foofd", null, new HashMap<String, String>());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFactoryNoDetector() throws Exception {
		FailureDetectorFactory factory = new FailureDetectorFactory();
		
		factory.createFd(null, null, new HashMap<String, String>());
	}
	
	@Test
	public void testFactoryByName() throws Exception {
		FailureDetectorFactory factory = new FailureDetectorFactory();
		
		FailureDetector fixedFd = factory.createFd(
				"fixed", null, new HashMap<String, String>());
		
		Assert.assertEquals(FixedPingFailureDetector.class, fixedFd.getClass());
		
		FailureDetector chenFd = factory.createFd(
				"chen", null, new HashMap<String, String>());
		
		Assert.assertEquals(ChenFailureDetector.class, chenFd.getClass());
		
		FailureDetector bertierFd = factory.createFd(
				"bertier", null, new HashMap<String, String>());
		
		Assert.assertEquals(BertierFailureDetector.class, bertierFd.getClass());
		
		FailureDetector phiAccrualFd = factory.createFd(
				"phiaccrual", null, new HashMap<String, String>());
		
		Assert.assertEquals(PhiAccrualFailureDetector.class, phiAccrualFd.getClass());
		
		FailureDetector slicedFd = factory.createFd(
				"sliced", null, new HashMap<String, String>());
		
		Assert.assertEquals(SlicedPingFailureDetector.class, slicedFd.getClass());
	}
	
	@Test
	public void testFactoryByClass() throws Exception {
		FailureDetectorFactory factory = new FailureDetectorFactory();
		
		FailureDetector fixedFd = factory.createFd(
				"myFD", FixedPingFailureDetector.class.getName(), 
				new HashMap<String, String>());
		
		Assert.assertEquals(FixedPingFailureDetector.class, fixedFd.getClass());
		
	}
	
}

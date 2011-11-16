package br.edu.ufcg.lsd.oursim.queue;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.events.EventSpec;

public class FileEventProxyTest {

	@Test
	public void testBlankFile() {
		FileEventProxy proxy = new FileEventProxy(
				getClass().getResourceAsStream("event-blank.conf"));
		
		Assert.assertFalse(proxy.hasNextEvent());
		Assert.assertNull(proxy.nextEventTime());
	}
	
	@Test
	public void testPaging() {
		FileEventProxy proxy = new FileEventProxy(
				getClass().getResourceAsStream("event-example.conf"));
		
		Assert.assertTrue(proxy.hasNextEvent());
		Assert.assertNotNull(proxy.nextEventTime());
		
		List<EventSpec> page1 = proxy.nextEventPage(5);
		
		Assert.assertEquals(5, page1.size());
		Assert.assertEquals(100, page1.get(0).getTime());
		Assert.assertEquals(500, page1.get(4).getTime());
		
		List<EventSpec> page2 = proxy.nextEventPage(5);
		
		Assert.assertEquals(5, page2.size());
		Assert.assertEquals(600, page2.get(0).getTime());
		Assert.assertEquals(23000, page2.get(4).getTime());
		
		Assert.assertFalse(proxy.hasNextEvent());
		Assert.assertNull(proxy.nextEventTime());
	}
	
	@Test
	public void testLastPage() {
		FileEventProxy proxy = new FileEventProxy(
				getClass().getResourceAsStream("event-example.conf"));
		
		Assert.assertTrue(proxy.hasNextEvent());
		Assert.assertNotNull(proxy.nextEventTime());
		
		proxy.nextEventPage(8);
		List<EventSpec> lastPage = proxy.nextEventPage(8);
		
		Assert.assertEquals(2, lastPage.size());
		Assert.assertFalse(proxy.hasNextEvent());
		Assert.assertNull(proxy.nextEventTime());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBadFormat() {
		new FileEventProxy(
				getClass().getResourceAsStream("event-bad.conf"));
	}
	
}

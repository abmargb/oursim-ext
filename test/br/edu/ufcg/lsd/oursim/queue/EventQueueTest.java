package br.edu.ufcg.lsd.oursim.queue;

import org.junit.Assert;
import org.junit.Test;

public class EventQueueTest {

	@Test
	public void testTimeOrdering() {
		ListEventProxy evProxy = new ListEventProxy();
		
		BlankEvent ev1 = new BlankEvent(10L);
		evProxy.add(ev1);
		
		BlankEvent ev2 = new BlankEvent(15L);
		evProxy.add(ev2);
		
		BlankEvent ev3 = new BlankEvent(20L);
		evProxy.add(ev3);
		
		EventQueue queue = new EventQueue(evProxy);
		
		Assert.assertEquals(ev1, queue.poll());
		Assert.assertEquals(ev2, queue.poll());
		Assert.assertEquals(ev3, queue.poll());
	}
	
	@Test
	public void testPriorityOrdering() {
		ListEventProxy evProxy = new ListEventProxy();
		
		BlankEvent ev1 = new BlankEvent(10L, 1);
		evProxy.add(ev1);
		
		BlankEvent ev2 = new BlankEvent(10L);
		evProxy.add(ev2);
		
		BlankEvent ev3 = new BlankEvent(10L, 2);
		evProxy.add(ev3);
		
		EventQueue queue = new EventQueue(evProxy);
		
		Assert.assertEquals(ev3, queue.poll());
		Assert.assertEquals(ev1, queue.poll());
		Assert.assertEquals(ev2, queue.poll());
	}
	
}

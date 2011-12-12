package br.edu.ufcg.lsd.oursim.queue;

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.lsd.oursim.factories.EventFactory;

public class EventQueueTest {

	@Test
	public void testTimeOrdering() {
		EventQueue queue = new EventQueue(
				new ListEventProxy(), new EventFactory());
		
		BlankEvent ev1 = new BlankEvent(10L);
		queue.add(ev1);
		
		BlankEvent ev2 = new BlankEvent(15L);
		queue.add(ev2);
		
		BlankEvent ev3 = new BlankEvent(20L);
		queue.add(ev3);
		
		
		Assert.assertEquals(ev1, queue.poll());
		Assert.assertEquals(ev2, queue.poll());
		Assert.assertEquals(ev3, queue.poll());
	}
	
	@Test
	public void testPriorityOrdering() {
		EventQueue queue = new EventQueue(
				new ListEventProxy(), new EventFactory());
		
		BlankEvent ev1 = new BlankEvent(10L, 1);
		queue.add(ev1);
		
		BlankEvent ev2 = new BlankEvent(10L);
		queue.add(ev2);
		
		BlankEvent ev3 = new BlankEvent(10L, 2);
		queue.add(ev3);
		
		
		Assert.assertEquals(ev3, queue.poll());
		Assert.assertEquals(ev1, queue.poll());
		Assert.assertEquals(ev2, queue.poll());
	}
	
	@Test
	public void testAddFirst() {
		EventQueue queue = new EventQueue(
				new ListEventProxy(), new EventFactory());
		
		BlankEvent ev1 = new BlankEvent(10L, 1);
		queue.add(ev1);
		
		BlankEvent ev2 = new BlankEvent(10L);
		queue.add(ev2);
		
		Assert.assertEquals(ev1, queue.poll());
		Assert.assertEquals(ev2, queue.poll());
	}
	
	@Test
	public void testAddAfter() {
		EventQueue queue = new EventQueue(
				new ListEventProxy(), new EventFactory());
		
		BlankEvent ev2 = new BlankEvent(10L, 0);
		queue.add(ev2);
		
		BlankEvent ev1 = new BlankEvent(10L, 1);
		queue.add(ev1);
		
		Assert.assertEquals(ev1, queue.poll());
		Assert.assertEquals(ev2, queue.poll());
	}
	
	@Test
	public void testFIFOOrdering() {
		EventQueue queue = new EventQueue(
				new ListEventProxy(), new EventFactory());
		
		BlankEvent ev1 = new BlankEvent(10L);
		queue.add(ev1);
		
		BlankEvent ev2 = new BlankEvent(10L);
		queue.add(ev2);
		
		BlankEvent ev3 = new BlankEvent(10L);
		queue.add(ev3);
		
		
		Assert.assertEquals(ev1, queue.poll());
		Assert.assertEquals(ev2, queue.poll());
		Assert.assertEquals(ev3, queue.poll());
	}
}

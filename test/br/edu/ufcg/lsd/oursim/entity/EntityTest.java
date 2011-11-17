package br.edu.ufcg.lsd.oursim.entity;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.entities.Entity;

public class EntityTest {

	@Test
	public void testSettersAndGetters() {
		Entity entity = new Entity();
		entity.setData("key", "value");
		
		Assert.assertEquals("value", entity.getData("key"));
		Assert.assertNull(entity.getData("noKey"));
	}
	
}

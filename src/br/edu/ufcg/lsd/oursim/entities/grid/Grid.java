package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Entity;

public class Grid extends Entity {

	private Map<String, ActiveEntity> objectRepository = new HashMap<String, ActiveEntity>();
	
	public void addObject(ActiveEntity object) {
		objectRepository.put(object.getId(), object);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ActiveEntity> T getObject(String id) {
		return (T) objectRepository.get(id);
	}

	public Collection<ActiveEntity> getAllObjects() {
		return objectRepository.values();
	}
}

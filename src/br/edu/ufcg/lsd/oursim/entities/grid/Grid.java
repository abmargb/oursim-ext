package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Entity;

public class Grid extends Entity {

	private Map<String, ActiveEntity> objectRepository = new HashMap<String, ActiveEntity>();
	private DiscoveryService discoveryService;
	
	public DiscoveryService getDiscoveryService() {
		return discoveryService;
	}
	
	public void setDiscoveryService(DiscoveryService discoveryService) {
		this.discoveryService = discoveryService;
	}
	
	public void addObject(ActiveEntity object) {
		objectRepository.put(object.getId(), object);
	}
	
	public ActiveEntity getObject(String id) {
		return objectRepository.get(id);
	}

	public Collection<ActiveEntity> getAllObjects() {
		return objectRepository.values();
	}
}

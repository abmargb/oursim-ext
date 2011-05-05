package br.edu.ufcg.lsd.oursim.entities;

import java.util.HashMap;
import java.util.Map;

public class Entity {

	private Map<String, Object> data = new HashMap<String, Object>();

	
	@SuppressWarnings("unchecked")
	public <T> T getData(String key) {
		return (T) data.get(key);
	}

	public void setData(String key, Object value) {
		data.put(key, value);
	}
}

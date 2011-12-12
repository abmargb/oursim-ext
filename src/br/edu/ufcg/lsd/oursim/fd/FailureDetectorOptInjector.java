package br.edu.ufcg.lsd.oursim.fd;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class FailureDetectorOptInjector {

	private final FailureDetectorFactory fdFactory = new FailureDetectorFactory();
	
	public void inject(Grid grid, Properties properties) {
		Long timeout = Long.parseLong(properties.getProperty(
				Configuration.PROP_FAILURE_DETECTOR_TIMEOUT));
		
		Long pingInterval = Long.parseLong(properties.getProperty(
				Configuration.PROP_FAILURE_DETECTOR_PING_INTERVAL));
		
		for (ActiveEntity entity : grid.getAllObjects()) {
			entity.setFailureDetector(createFd(properties));
			entity.setTimeout(timeout);
			entity.setPingInterval(pingInterval);
		}
	}

	private FailureDetector createFd(Properties properties) {
		Boolean useFd = Boolean.parseBoolean(properties.getProperty(
				Configuration.PROP_USE_FAILURE_DETECTOR));
		
		if (!useFd) {
			return null;
		}
		
		String fdName = properties.getProperty(
				Configuration.PROP_FAILURE_DETECTOR_NAME);
		try {
			return fdFactory.createFd(fdName, null, 
					getFdProperties(fdName, properties));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> getFdProperties(String fdName, Properties properties) {
		Map<String, String> fdProperties = new HashMap<String, String>();
		String fdPrefix = "FD_" + fdName + "_";
		
		for (Object key : properties.keySet()) {
			String strKey = (String)key;
			if (strKey.startsWith(fdPrefix)) {
				String fdKey = strKey.substring(fdPrefix.length());
				fdProperties.put(fdKey, properties.getProperty(strKey));
			}
		}
		return fdProperties;
	}
	
}

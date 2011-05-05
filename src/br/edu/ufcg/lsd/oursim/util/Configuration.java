package br.edu.ufcg.lsd.oursim.util;

import java.util.Properties;

public class Configuration {

	public static final String PROP_LIVENESS_CHECK_INTERVAL = "LIVENESS_CHECK_INTERVAL";
	public static final String DEF_LIVENESS_CHECK_INTERVAL = "100";

	public static final String PROP_USE_FAILURE_DETECTOR = "USE_FAILURE_DETECTOR";
	public static final String DEF_USE_FAILURE_DETECTOR = Boolean.TRUE.toString();
	
	public static Properties createDefault() {
		Properties properties = new Properties();
		properties.put(PROP_LIVENESS_CHECK_INTERVAL, DEF_LIVENESS_CHECK_INTERVAL);
		properties.put(PROP_USE_FAILURE_DETECTOR, DEF_USE_FAILURE_DETECTOR);
		return properties;
	}
	
}

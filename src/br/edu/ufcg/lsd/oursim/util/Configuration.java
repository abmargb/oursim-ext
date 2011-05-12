package br.edu.ufcg.lsd.oursim.util;

import java.util.Properties;

public class Configuration {

	public static final String PROP_LIVENESS_CHECK_INTERVAL = "LIVENESS_CHECK_INTERVAL";
	public static final String DEF_LIVENESS_CHECK_INTERVAL = "1000";
	
	public static final String PROP_USE_FAILURE_DETECTOR = "USE_FAILURE_DETECTOR";
	public static final String DEF_USE_FAILURE_DETECTOR = Boolean.TRUE.toString();
	
	public static final String PROP_BROKER_MAX_FAILS = "BROKER_MAX_FAILS";
	public static final String DEF_BROKER_MAX_FAILS = "3";
	
	public static final String PROP_BROKER_MAX_REPLICAS = "BROKER_MAX_REPLICAS";
	public static final String DEF_BROKER_MAX_REPLICAS = "3";
	
	public static final String PROP_BROKER_SCHEDULER_INTERVAL = "BROKER_SCHEDULER_INTERVAL";
	public static final String DEF_BROKER_SCHEDULER_INTERVAL = "10000";
	
	public static final String PROP_REQUEST_REPETITION_INTERVAL = "REQUEST_REPETITION_INTERVAL";
	public static final String DEF_REQUEST_REPETITION_INTERVAL = "30000";
	
	public static Properties createDefault() {
		Properties properties = new Properties();
		properties.put(PROP_LIVENESS_CHECK_INTERVAL, DEF_LIVENESS_CHECK_INTERVAL);
		properties.put(PROP_USE_FAILURE_DETECTOR, DEF_USE_FAILURE_DETECTOR);
		properties.put(PROP_BROKER_MAX_FAILS, DEF_BROKER_MAX_FAILS);
		properties.put(PROP_BROKER_MAX_REPLICAS, DEF_BROKER_MAX_REPLICAS);
		properties.put(PROP_BROKER_SCHEDULER_INTERVAL, DEF_BROKER_SCHEDULER_INTERVAL);
		properties.put(PROP_REQUEST_REPETITION_INTERVAL, DEF_REQUEST_REPETITION_INTERVAL);
		return properties;
	}
	
}

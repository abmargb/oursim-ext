package br.edu.ufcg.lsd.oursim.util;

import java.util.Properties;

public class Configuration {

	public static final String PROP_LIVENESS_CHECK_INTERVAL = "LIVENESS_CHECK_TOLERANCE";
	public static final String DEF_LIVENESS_CHECK_INTERVAL = "30000";
	
	public static final String PROP_USE_FAILURE_DETECTOR = "USE_FAILURE_DETECTOR";
	public static final String DEF_USE_FAILURE_DETECTOR = Boolean.TRUE.toString();
	
	// Less time, more memory
	public static final String PROP_USE_SPEED_HACK = "USE_SPEED_HACK";
	public static final String DEF_USE_SPEED_HACK = Boolean.FALSE.toString();
	
	public static final String PROP_BROKER_MAX_FAILS = "BROKER_MAX_FAILS";
	public static final String DEF_BROKER_MAX_FAILS = "3";
	
	public static final String PROP_BROKER_MAX_REPLICAS = "BROKER_MAX_REPLICAS";
	public static final String DEF_BROKER_MAX_REPLICAS = "3";
	
	public static final String PROP_BROKER_MAX_SIMULTANEOUS_REPLICAS = "BROKER_MAX_SIMULTANEOUS_REPLICAS";
	public static final String DEF_BROKER_MAX_SIMULTANEOUS_REPLICAS = "3";
	
	public static final String PROP_BROKER_SCHEDULER_INTERVAL = "BROKER_SCHEDULER_INTERVAL";
	public static final String DEF_BROKER_SCHEDULER_INTERVAL = "10000";
	
	public static final String PROP_REQUEST_REPETITION_INTERVAL = "REQUEST_REPETITION_INTERVAL";
	public static final String DEF_REQUEST_REPETITION_INTERVAL = "120000";
	
	public static final String PROP_GET_PROVIDERS_REPETITION_INTERVAL = "GET_PROVIDERS_REPETITION_INTERVAL";
	public static final String DEF_GET_PROVIDERS_REPETITION_INTERVAL = "60000";
	
	public static final String PROP_REPORT_WORK_ACCOUNTING_REPETITION_INTERVAL = "REPORT_WORK_ACCOUNTING_REPETITION_INTERVAL";
	public static final String DEF_REPORT_WORK_ACCOUNTING_REPETITION_INTERVAL = "120000";
	
	public static final String PROP_FAILURE_DETECTOR_NAME = "FD_NAME";
	public static final String DEF_FAILURE_DETECTOR_NAME = "fixed";
	
	public static final String PROP_FAILURE_DETECTOR_TIMEOUT = "FD_TIMEOUT";
	public static final String DEF_FAILURE_DETECTOR_TIMEOUT = "300000";
	
	public static final String PROP_FAILURE_DETECTOR_PING_INTERVAL = "FD_PING_INTERVAL";
	public static final String DEF_FAILURE_DETECTOR_PING_INTERVAL = "60000";
	
	public static Properties createDefaults() {
		Properties properties = new Properties();
		properties.put(PROP_LIVENESS_CHECK_INTERVAL, DEF_LIVENESS_CHECK_INTERVAL);
		properties.put(PROP_USE_FAILURE_DETECTOR, DEF_USE_FAILURE_DETECTOR);
		properties.put(PROP_BROKER_MAX_FAILS, DEF_BROKER_MAX_FAILS);
		properties.put(PROP_BROKER_MAX_REPLICAS, DEF_BROKER_MAX_REPLICAS);
		properties.put(PROP_BROKER_MAX_SIMULTANEOUS_REPLICAS, DEF_BROKER_MAX_SIMULTANEOUS_REPLICAS);
		properties.put(PROP_BROKER_SCHEDULER_INTERVAL, DEF_BROKER_SCHEDULER_INTERVAL);
		properties.put(PROP_REQUEST_REPETITION_INTERVAL, DEF_REQUEST_REPETITION_INTERVAL);
		properties.put(PROP_GET_PROVIDERS_REPETITION_INTERVAL, DEF_GET_PROVIDERS_REPETITION_INTERVAL);
		properties.put(PROP_REPORT_WORK_ACCOUNTING_REPETITION_INTERVAL, DEF_REPORT_WORK_ACCOUNTING_REPETITION_INTERVAL);
		properties.put(PROP_FAILURE_DETECTOR_NAME, DEF_FAILURE_DETECTOR_NAME);
		properties.put(PROP_FAILURE_DETECTOR_TIMEOUT, DEF_FAILURE_DETECTOR_TIMEOUT);
		properties.put(PROP_FAILURE_DETECTOR_PING_INTERVAL, DEF_FAILURE_DETECTOR_PING_INTERVAL);
		properties.put(PROP_USE_SPEED_HACK, DEF_USE_SPEED_HACK);
		
		return properties;
	}
	
	public static Properties createConfiguration(Properties properties) {
		Properties defaults = new Properties(createDefaults());
		defaults.putAll(properties);
		return defaults;
	}
	
}

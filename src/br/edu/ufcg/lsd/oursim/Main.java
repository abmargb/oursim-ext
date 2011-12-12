package br.edu.ufcg.lsd.oursim;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import br.edu.ufcg.lsd.oursim.factories.JsonGridFactory;
import br.edu.ufcg.lsd.oursim.network.BlankNetwork;
import br.edu.ufcg.lsd.oursim.queue.FileEventProxy;
import br.edu.ufcg.lsd.oursim.trace.DefaultTraceCollector;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		long beginning = System.currentTimeMillis();
		
		Properties properties = Configuration.createConfiguration(new Properties());
		properties.put(Configuration.PROP_BROKER_MAX_REPLICAS, "1");
		properties.put(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.FALSE.toString());
		properties.put(Configuration.PROP_BROKER_SCHEDULER_INTERVAL, "10");
		
		OurSim ourSim = new OurSim(
				new FileEventProxy(new FileInputStream("resources/event-example.conf")), 
				new JsonGridFactory(properties, new FileInputStream("resources/grid-example.conf")).createGrid(),
				properties,
				new BlankNetwork(),
				new DefaultTraceCollector(new FileOutputStream("trace.out")));
		
		ourSim.run();
		
		long end = System.currentTimeMillis();
		
		System.out.println("Sim time: " + (end-beginning)/1000/60 + " minutes.");
	}
	
}

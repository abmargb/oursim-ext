package br.edu.ufcg.lsd.oursim;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import br.edu.ufcg.lsd.oursim.factories.DefaultGridFactory;
import br.edu.ufcg.lsd.oursim.network.DefaultWANNetwork;
import br.edu.ufcg.lsd.oursim.queue.DefaultEventProxy;
import br.edu.ufcg.lsd.oursim.trace.DefaultTraceCollector;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Properties properties = Configuration.createConfiguration(new Properties());
//		properties.put(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.FALSE.toString());
		
		OurSim ourSim = new OurSim(
				new DefaultEventProxy(new FileInputStream("resources/event-example.conf")), 
				new DefaultGridFactory(properties, new FileInputStream("resources/grid-example.conf")),
				properties,
				new DefaultWANNetwork(),
				new DefaultTraceCollector(new FileOutputStream("trace.out")));
		
		ourSim.run();
	}
	
}

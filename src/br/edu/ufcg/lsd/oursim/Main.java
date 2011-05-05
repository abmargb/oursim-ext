package br.edu.ufcg.lsd.oursim;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import br.edu.ufcg.lsd.oursim.factories.DefaultGridFactory;
import br.edu.ufcg.lsd.oursim.network.DefaultWANNetwork;
import br.edu.ufcg.lsd.oursim.queue.DefaultEventProxy;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Properties properties = new Properties();
//		properties.put(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.FALSE.toString());
		
		OurSim ourSim = new OurSim(
				new DefaultEventProxy(new FileInputStream("resources/event-example.conf")), 
				new DefaultGridFactory(new FileInputStream("resources/grid-example.conf")),
				properties,
				new DefaultWANNetwork());
		
		ourSim.run();
	}
	
}

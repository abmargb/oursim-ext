package br.edu.ufcg.lsd.oursim.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.DiscoveryService;
import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.ds.DiscoveryServiceEvents;
import br.edu.ufcg.lsd.oursim.events.global.HaltCondition;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;
import br.edu.ufcg.lsd.oursim.fd.FailureDetectorOptInjector;
import br.edu.ufcg.lsd.oursim.network.DefaultWANNetwork;
import br.edu.ufcg.lsd.oursim.queue.ListEventProxy;
import br.edu.ufcg.lsd.oursim.trace.DefaultTraceCollector;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class FDScenarioGenerator {

	static double LOCALHOST_DELAY = 500.;
	static int WORKERS_PER_SITE = 50;
	static int JOBS_PER_BROKER = 1;
	
	static String SCENARIO = "fd";
	
	public static void main(String[] args) throws Exception {
	
		Properties properties = Configuration.createConfiguration(new Properties());
		properties.put(Configuration.PROP_BROKER_MAX_REPLICAS, "3");
		properties.put(Configuration.PROP_BROKER_MAX_SIMULTANEOUS_REPLICAS, "1");
		properties.put(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.TRUE.toString());
		properties.put(Configuration.PROP_BROKER_SCHEDULER_INTERVAL, "10000");
		
		if (args[0].equals("fixed")) {
			properties.put(Configuration.PROP_FAILURE_DETECTOR_NAME, "fixed");
		} else if(args[0].equals("chen")) {
			properties.put(Configuration.PROP_FAILURE_DETECTOR_NAME, "chen");
			properties.put("FD_chen_alpha", "5000");
		} else if(args[0].equals("bertier")) {
			properties.put(Configuration.PROP_FAILURE_DETECTOR_NAME, "bertier");
			properties.put("FD_bertier_beta", "1");
			properties.put("FD_bertier_gamma", "0.1");
			properties.put("FD_bertier_phi", "4");
		} else if(args[0].equals("phi")) {
			properties.put(Configuration.PROP_FAILURE_DETECTOR_NAME, "phiaccrual");
			properties.put("FD_phiaccrual_threshold", "0.5");
		}
		
		run(properties, args[0]);
	}
	
	private static void run(Properties properties, String fdName)
			throws JSONException, IOException {
		
		System.out.println("Running...");
		
		ListEventProxy evProxy = new ListEventProxy();
		
		Grid grid = new Grid();
		
		DiscoveryService ds = new DiscoveryService();
		ds.setId("ds");
		grid.addObject(ds);
		evProxy.add(new EventSpec(DiscoveryServiceEvents.DS_UP, 0, ds.getId()));
		
		Map<String, Broker> brokers = new HashMap<String, Broker>();
		Map<String, Peer> peers = new HashMap<String, Peer>();
		List<JSONObject> jobs = new LinkedList<JSONObject>();
		
		List<String> lines = IOUtils.readLines(new FileInputStream("resources/iosup_workload_7_dias_50_sites.txt"));
		
		boolean first = true;
		
		for (String line : lines) {
			if (first) {
				first = false;
				continue;
			}
			
			String[] splittedLined = line.split(" ");
			String brokerIdx = splittedLined[5];
			String brokerId = "broker" + brokerIdx;
			
			String peerIdx = splittedLined[6];
			String peerId = "peer" + peerIdx;
			
			if (!brokers.containsKey(brokerId)) {
				Broker broker = new Broker();
				broker.setId(brokerId);
				broker.setPeerId(peerId);
				
				grid.addObject(broker);
				brokers.put(broker.getId(), broker);
			}
			
			if (!peers.containsKey(peerId)) {
				
				Peer peer = new Peer();
				peer.setId(peerId);
				peer.setDiscoveryServiceId(ds.getId());
				grid.addObject(peer);
				
				for (int j = 0; j < 50; j++) {
					Worker worker = new Worker();
					worker.setId("worker" + j + peer.getId());
					worker.setCpu(1.);
					
					grid.addObject(worker);
					peer.addWorker(worker.getId());
					
					evProxy.add(new EventSpec(WorkerEvents.WORKER_UP, 10, worker.getId()));
				}
				
				peers.put(peer.getId(), peer);
			}
			
			peers.get(peerId).addBroker(brokerId);
			
			String time = splittedLined[0];
			String jobId = splittedLined[1];
			String[] tasks = splittedLined[4].split(";");
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", Integer.valueOf(jobId));
			jsonObject.put("time", Integer.valueOf(time) * 1000);
			jsonObject.put("broker", brokerId);
			
			JSONArray tasksArray = new JSONArray();
			jsonObject.put("tasks", tasksArray);
			
			for (String task : tasks) {
				JSONObject jsonTask = new JSONObject();
				jsonTask.put("duration", Integer.valueOf(task) * 1000);
				tasksArray.put(jsonTask);
			}
			
			jobs.add(jsonObject);
		}
		

		for (Peer peer : peers.values()) {
			evProxy.add(new EventSpec(PeerEvents.PEER_UP, 20, peer.getId()));
		}
		
		for (Broker broker : brokers.values()) {
			evProxy.add(new EventSpec(BrokerEvents.BROKER_UP, 30, broker.getId()));
		}
		
		for (JSONObject job : jobs) {
			evProxy.add(new EventSpec(BrokerEvents.ADD_JOB, job.getInt("time"), job.getString("broker") + " " + job.toString()));
		}
		
		
		File parentFile = new File(
				"resources/experiments/" + SCENARIO + "/fd-" + fdName);
		
		parentFile.mkdirs();
		
		new FailureDetectorOptInjector().inject(grid, properties);
		
		File traceFile = new File(parentFile.getAbsolutePath() +"/trace.out");
		
		OurSim ourSim = new OurSim(
				evProxy, 
				grid,
				properties,
				new DefaultWANNetwork(1./LOCALHOST_DELAY),
				new DefaultTraceCollector(
						new FileOutputStream(traceFile)));
		
		ourSim.addEventListener(new JobFinishedCondition(ourSim));
		ourSim.run();
	}
	 
	private static class JobFinishedCondition extends HaltCondition {

		public JobFinishedCondition(OurSim ourSim) {
			super(ourSim);
		}

		@Override
		public boolean halt(Event lastEvent, OurSim ourSim) {
			
			if (lastEvent.getTime() > 600000000) {
				return true;
			}
			
			return false;
		}
		
	}
}


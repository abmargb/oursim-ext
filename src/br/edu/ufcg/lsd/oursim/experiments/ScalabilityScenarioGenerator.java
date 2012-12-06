package br.edu.ufcg.lsd.oursim.experiments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
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

public class ScalabilityScenarioGenerator {

	private static final String BROKER_ID = "brokerA";
	private static final int REPLICATION = 1;
	static int TASKS_DURATION = 10000; 
	static double LOCALHOST_DELAY = 5.;
	
	static int WORKERS_PER_SITE = 50;
	static int CONTENTION_LEVEL = 1;
	
	static int[] SITES = new int[]{1, 5, 10, 50, 100, 500, 1000, 5000, 10000};
	static int JOBS_PER_BROKER = 1;
	
	static String SCENARIO = "scalability";
	
	static int[][] SEEDS = new int[][]{
		new int[]{214338003, 944166635, 570782134, 526559949, 919594043, 214338003}, 
		new int[]{413630306, 617652072, 984949403, 2098010699, 1519794858, 413630306}, 
		new int[]{70395995, 2035636432, 1898631199, 1140526672, 787341018, 70395995}, 
		new int[]{1146986511, 1886216422, 282759243, 1356582152, 538696788, 1146986511}, 
		new int[]{1303202085, 1074684734, 2004358020, 30184504, 1102953255, 1303202085} 
	};
	
	public static void main(String[] args) throws Exception {
	
		Properties properties = Configuration.createConfiguration(new Properties());
		properties.put(Configuration.PROP_BROKER_MAX_REPLICAS, "1");
		properties.put(Configuration.PROP_BROKER_MAX_SIMULTANEOUS_REPLICAS, "1");
		properties.put(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.TRUE.toString());
		properties.put(Configuration.PROP_BROKER_SCHEDULER_INTERVAL, "10000");
		properties.put(Configuration.PROP_FAILURE_DETECTOR_NAME, "fixed");
		properties.put(Configuration.PROP_USE_SPEED_HACK, Boolean.TRUE.toString());
		
		for (int siteIdx = 0; siteIdx < SITES.length; siteIdx++) {
			run(properties, SITES[siteIdx]);
		}
	}

	private static void run(Properties properties, int sitesNo)
			throws JSONException, IOException {
		for (int i = 0; i < REPLICATION; i++) {
			DefaultWANNetwork.setSeed(SEEDS[i]);
			run(properties, WORKERS_PER_SITE, sitesNo, sitesNo * WORKERS_PER_SITE * CONTENTION_LEVEL, i);
		}
	}
	
	private static void run(Properties properties, int workersNo, int sitesNo, int tasksNo, int replica)
			throws JSONException, IOException {
		
		System.out.println("Running w" + workersNo + ", t" + tasksNo + ", s" + sitesNo + ", r" + replica);
		
		ListEventProxy evProxy = new ListEventProxy();
		
		Grid grid = new Grid();
		
		DiscoveryService ds = new DiscoveryService();
		ds.setId("ds");
		grid.addObject(ds);
		evProxy.add(new EventSpec(DiscoveryServiceEvents.DS_UP, 0, ds.getId()));
		
		List<Peer> peers = new LinkedList<Peer>();
		
		for (int i = 0; i < sitesNo; i++) {
			Peer peer = new Peer();
			peer.setId("peer" + i);
			peer.setDiscoveryServiceId(ds.getId());
			grid.addObject(peer);
			
			peers.add(peer);
			
			for (int j = 0; j < workersNo; j++) {
				Worker worker = new Worker();
				worker.setId("worker" + j + peer.getId());
				worker.setCpu(1.);
				
				grid.addObject(worker);
				peer.addWorker(worker.getId());
				
				evProxy.add(new EventSpec(WorkerEvents.WORKER_UP, 10, worker.getId()));
			}
		}

		Peer peerA = peers.iterator().next();
		
		Broker brokerA = new Broker();
		brokerA.setId(BROKER_ID);
		brokerA.setPeerId(peerA.getId());
		grid.addObject(brokerA);
		
		peerA.addBroker(brokerA.getId());
		
		for (Peer peer : peers) {
			evProxy.add(new EventSpec(PeerEvents.PEER_UP, 20, peer.getId()));
		}
		
		evProxy.add(new EventSpec(BrokerEvents.BROKER_UP, 20, brokerA.getId()));
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 1);
		JSONArray tasksArray = new JSONArray();
		jsonObject.put("tasks", tasksArray);
		
		for (int i = 0; i < tasksNo; i++) {
			JSONObject jsonTask = new JSONObject();
			jsonTask.put("duration", TASKS_DURATION);
			tasksArray.put(jsonTask);
		}
		
		evProxy.add(new EventSpec(BrokerEvents.ADD_JOB, 500, brokerA.getId() + " " + jsonObject.toString()));
		
		File parentFile = new File(
				"resources/experiments/" + SCENARIO + "/s"+sitesNo);
		
		parentFile.mkdirs();
		
		new FailureDetectorOptInjector().inject(grid, properties);
		
		File traceFile = new File(parentFile.getAbsolutePath() +"/trace-r" + replica + ".out");
		
		if (traceFile.exists()) {
			return;
		}
		
		OurSim ourSim = new OurSim(
				evProxy, 
				grid,
				properties,
				new DefaultWANNetwork(1/LOCALHOST_DELAY),
				new DefaultTraceCollector(
						new FileOutputStream(traceFile)));
		
		ourSim.addEventListener(new JobFinishedCondition(ourSim, JOBS_PER_BROKER));
		
		File timeFile = new File(parentFile.getAbsolutePath() +"/time-r" + replica + ".out");
		long begin = System.currentTimeMillis();
		ourSim.run();
		long time = System.currentTimeMillis() - begin;
		
		System.out.println("Simulation time: " + time);
		IOUtils.write(String.valueOf(time), new FileOutputStream(timeFile));
	}
	 
	private static class JobFinishedCondition extends HaltCondition {

		private final int finishedJobs;
		int msgs = 0;

		public JobFinishedCondition(OurSim ourSim, int finishedJobs) {
			super(ourSim);
			this.finishedJobs = finishedJobs;
		}

		@Override
		public boolean halt(Event lastEvent, OurSim ourSim) {
			
			msgs++;
			
			if (msgs % 100000 == 0) {
				System.out.println(msgs);
			}
			
			if (!lastEvent.getType().equals(BrokerEvents.HERE_IS_EXECUTION_RESULT)) {
				return false;
			}
			
			boolean halt = true;
			
			List<Job> finishedJobsList = new LinkedList<Job>();
			Broker broker = ourSim.getGrid().getObject(BROKER_ID);

			List<Job> jobs = broker.getJobs();
			int currentlyFinished = 0;

			for (Job job : jobs) {
				if (job.getState().equals(ExecutionState.FINISHED)) {
					currentlyFinished++;
					finishedJobsList.add(job);
				}
			}

			halt &= currentlyFinished >= finishedJobs;

			if (halt) {
				for (Job job : finishedJobsList) {
					System.out.println(job.getEndTime());
				}
			}

			return halt;
		}
		
	}
}


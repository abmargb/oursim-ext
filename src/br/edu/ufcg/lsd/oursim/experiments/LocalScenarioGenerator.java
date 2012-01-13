package br.edu.ufcg.lsd.oursim.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.entities.job.ExecutionState;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.events.broker.BrokerEvents;
import br.edu.ufcg.lsd.oursim.events.global.HaltCondition;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;
import br.edu.ufcg.lsd.oursim.fd.FailureDetectorOptInjector;
import br.edu.ufcg.lsd.oursim.network.DefaultWANNetwork;
import br.edu.ufcg.lsd.oursim.queue.ListEventProxy;
import br.edu.ufcg.lsd.oursim.trace.DefaultTraceCollector;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class LocalScenarioGenerator {

	static int MAX_WORKERS = 4;
	static int[] TASKS = new int[]{1, 2, 4, 20, 100}; 
	static int TASKS_DURATION = 10000; 
	static double LOCALHOST_DELAY = 5.;
	
	static int[][] SEEDS = new int[][]{
		new int[]{214338003, 944166635, 570782134, 526559949, 919594043, 214338003}, 
		new int[]{413630306, 617652072, 984949403, 2098010699, 1519794858, 413630306}, 
		new int[]{70395995, 2035636432, 1898631199, 1140526672, 787341018, 70395995}, 
		new int[]{1146986511, 1886216422, 282759243, 1356582152, 538696788, 1146986511}, 
		new int[]{1303202085, 1074684734, 2004358020, 30184504, 1102953255, 1303202085} 
	};
	
	public static void main(String[] args) throws Exception {
	
		Properties properties = Configuration.createConfiguration(new Properties());
		properties.put(Configuration.PROP_BROKER_MAX_REPLICAS, "3");
		properties.put(Configuration.PROP_BROKER_MAX_SIMULTANEOUS_REPLICAS, "1");
		properties.put(Configuration.PROP_USE_FAILURE_DETECTOR, Boolean.TRUE.toString());
		properties.put(Configuration.PROP_BROKER_SCHEDULER_INTERVAL, "10000");
		properties.put(Configuration.PROP_FAILURE_DETECTOR_NAME, "fixed");
		
		for (int taskIdx = 0; taskIdx < TASKS.length; taskIdx++) {
			
			int workersNo = MAX_WORKERS;
			int tasksNo = TASKS[taskIdx];
			run(properties, workersNo, tasksNo);
		}
		
		for (int workersNo = 1; workersNo <= MAX_WORKERS; workersNo++) {
			
			int tasksNo = TASKS[TASKS.length - 1]; 
			run(properties, workersNo, tasksNo);
		}
		
	}

	private static void run(Properties properties, int workersNo, int tasksNo)
			throws JSONException, FileNotFoundException {
		for (int i = 0; i < 5; i++) {
			DefaultWANNetwork.setSeed(SEEDS[i]);
			run(properties, workersNo, tasksNo, i);
		}
	}
	
	private static void run(Properties properties, int workersNo, int tasksNo, int replica)
			throws JSONException, FileNotFoundException {
		
		System.out.println("Running w" + workersNo + ", t" + tasksNo + ", r" + replica);
		
		ListEventProxy evProxy = new ListEventProxy();
		
		Grid grid = new Grid();
		
		Peer peer = new Peer();
		peer.setId("peer");
		grid.addObject(peer);
		evProxy.add(new EventSpec(PeerEvents.PEER_UP, 0, peer.getId()));
		
		Broker broker = new Broker();
		broker.setId("broker");
		broker.setPeerId(peer.getId());
		grid.addObject(broker);
		evProxy.add(new EventSpec(BrokerEvents.BROKER_UP, 0, broker.getId()));
		
		peer.addBroker(broker.getId());
		
		for (int i = 0; i < workersNo; i++) {
			Worker worker = new Worker();
			worker.setId("worker" + i);
			worker.setCpu(1.);
			
			grid.addObject(worker);
			peer.addWorker(worker.getId());
			
			evProxy.add(new EventSpec(WorkerEvents.WORKER_UP, 0, worker.getId()));
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", 1);
		JSONArray tasksArray = new JSONArray();
		jsonObject.put("tasks", tasksArray);
		
		for (int i = 0; i < tasksNo; i++) {
			JSONObject jsonTask = new JSONObject();
			jsonTask.put("duration", TASKS_DURATION);
			tasksArray.put(jsonTask);
		}
		
		evProxy.add(new EventSpec(BrokerEvents.ADD_JOB, 0, broker.getId() + " " + jsonObject.toString()));
		
		File parentFile = new File(
				"resources/experiments/local-singlebroker/w"+workersNo+"/t"+tasksNo);
		parentFile.mkdirs();
		
		new FailureDetectorOptInjector().inject(grid, properties);
		
		OurSim ourSim = new OurSim(
				evProxy, 
				grid,
				properties,
				new DefaultWANNetwork(1/LOCALHOST_DELAY),
				new DefaultTraceCollector(
						new FileOutputStream(parentFile.getAbsolutePath() +"/trace-r" + replica + ".out")));
		
		ourSim.addEventListener(new JobFinishedCondition(ourSim, broker, 1));
		
		ourSim.run();
	}
	 
	private static class JobFinishedCondition extends HaltCondition {

		private final Broker broker;
		private final int finishedJobs;

		public JobFinishedCondition(OurSim ourSim, Broker broker, int finishedJobs) {
			super(ourSim);
			this.broker = broker;
			this.finishedJobs = finishedJobs;
		}

		@Override
		public boolean halt(Event lastEvent, OurSim ourSim) {
			List<Job> jobs = broker.getJobs();
			int currentlyFinished = 0;
			
			for (Job job : jobs) {
				if (job.getState().equals(ExecutionState.FINISHED)) {
					currentlyFinished++;
				}
			}
			
			boolean halt = currentlyFinished >= finishedJobs;
			
			if (halt) {
				for (Job job : jobs) {
					System.out.println(job.getEndTime());
				}
			}
			
			return halt;
		}
		
	}
}


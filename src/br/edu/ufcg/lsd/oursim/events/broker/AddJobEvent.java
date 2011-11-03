package br.edu.ufcg.lsd.oursim.events.broker;

import org.json.JSONArray;
import org.json.JSONObject;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.PrimaryEvent;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;
import br.edu.ufcg.lsd.oursim.util.JSONUtils;
import br.edu.ufcg.lsd.oursim.util.LineParser;

public class AddJobEvent extends PrimaryEvent {

	public AddJobEvent(String data) {
		super(Event.DEF_PRIORITY, data);
	}

	@Override
	public void process(OurSim ourSim) {
		LineParser lineParser = new LineParser(getData());
		String brokerId = lineParser.next();
		Job job = parseJob(lineParser.restOfLine());
		
		Broker broker = ourSim.getGrid().getObject(brokerId);
		if (!broker.isUp()) {
			return;
		}
		
		broker.addJob(job);

		if (broker.getPeerId() == null || 
				!broker.getMonitor(broker.getPeerId()).isUp()) {
			return;
		}
		
		RequestSpec requestSpec = new RequestSpec();
		requestSpec.setBrokerId(brokerId);
		requestSpec.setId(Math.abs(ourSim.getRandom().nextLong()));
		requestSpec.setRequiredWorkers(job.getTasks().size()
				* ourSim.getIntProperty(Configuration.PROP_BROKER_MAX_REPLICAS));
		
		BrokerRequest request = new BrokerRequest(requestSpec);
		request.setJob(job);
		job.setRequest(request);
		broker.addRequest(request);
		
		ourSim.addNetworkEvent(ourSim.createEvent(PeerEvents.REQUEST_WORKERS, getTime(), 
				broker.getPeerId(), request.getSpec(), false));
	}

	private Job parseJob(String jobJsonStr) {
		Job job = new Job();
		
		JSONObject jobJson = JSONUtils.asJSON(jobJsonStr);
		int jobId = JSONUtils.getJSONInteger(jobJson, "id");
		job.setId(jobId);
		job.setCreationTime(getTime());
		
		JSONArray taskArray = JSONUtils.getJSONArray(jobJson, "tasks");
		
		for (int i = 0; i < taskArray.length(); i++) {
			Task task = new Task();
			JSONObject taskJson = JSONUtils.getJSONObject(taskArray, i);
			long duration = JSONUtils.getJSONLong(taskJson, "duration");
			task.setDuration(duration);
			task.setJob(job);
			task.setId(i+1);
			task.setCreationTime(getTime());
			
			job.addTask(task);
		}
		
		return job;
	}
}

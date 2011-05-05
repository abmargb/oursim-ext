package br.edu.ufcg.lsd.oursim.events.broker;

import org.json.JSONArray;
import org.json.JSONObject;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.job.Task;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.util.JSONUtils;
import br.edu.ufcg.lsd.oursim.util.LineParser;

public class JobAddedEvent extends AbstractEvent {

	public static final String TYPE = "ADD_JOB";
	
	public JobAddedEvent(Long time, String data) {
		super(time, Event.DEF_PRIORITY, data);
	}

	@Override
	public void process(OurSim ourSim) {
		LineParser lineParser = new LineParser(getData());
		String brokerId = lineParser.next();
		Job job = parseJob(lineParser.restOfLine());

		Broker broker = (Broker) ourSim.getGrid().getObject(brokerId);
		broker.addJob(job);

		ourSim.addEvent(new BrokerScheduleEvent(getTime() + 1, brokerId));
	}

	private static Job parseJob(String jobJsonStr) {
		Job job = new Job();
		
		JSONObject jobJson = JSONUtils.asJSON(jobJsonStr);
		int jobId = JSONUtils.getJSONInteger(jobJson, "id");
		job.setId(jobId);
		
		JSONArray taskArray = JSONUtils.getJSONArray(jobJson, "tasks");
		
		for (int i = 0; i < taskArray.length(); i++) {
			Task task = new Task();
			JSONObject taskJson = JSONUtils.getJSONObject(taskArray, i);
			long duration = JSONUtils.getJSONLong(taskJson, "duration");
			task.setDuration(duration);
			task.setJob(job);
			
			job.addTask(task);
		}
		
		return job;
	}
}

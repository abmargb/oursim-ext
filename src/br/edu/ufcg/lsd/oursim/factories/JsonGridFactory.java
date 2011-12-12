package br.edu.ufcg.lsd.oursim.factories;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.grid.DiscoveryService;
import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.grid.Worker;
import br.edu.ufcg.lsd.oursim.fd.FailureDetectorOptInjector;
import br.edu.ufcg.lsd.oursim.util.JSONUtils;

public class JsonGridFactory implements GridFactory {

	private InputStream inputStream;
	private final Properties properties;
	
	/**
	 * @param inputStream
	 */
	public JsonGridFactory(Properties properties, InputStream inputStream) {
		this.properties = properties;
		this.inputStream = inputStream;
	}

	@Override
	public Grid createGrid() {
		
		JSONObject gridJson = null;
		try {
			String gridStr = IOUtils.toString(inputStream);
			gridJson = JSONUtils.asJSON(gridStr);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		
		Grid grid = new Grid();
		
		JSONObject dsJson = JSONUtils.getJSONObject(gridJson, "discoveryService");
		DiscoveryService ds = new DiscoveryService();
		ds.setId(JSONUtils.getJSONString(dsJson, "id"));
		
		grid.addObject(ds);
		
		JSONArray peersArray = JSONUtils.getJSONArray(gridJson, "peers");
		for (int i = 0; i < peersArray.length(); i++) {
			try {
				JSONObject peerJson = (JSONObject) peersArray.get(i);
				grid.addObject(createPeer(peerJson, grid));
			} catch (JSONException e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		JSONArray brokersArray = JSONUtils.getJSONArray(gridJson, "brokers");
		for (int i = 0; i < brokersArray.length(); i++) {
			try {
				JSONObject brokerJson = (JSONObject) brokersArray.get(i);
				grid.addObject(createBroker(brokerJson));
			} catch (JSONException e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		new FailureDetectorOptInjector().inject(grid, properties);
		
		return grid;
	}

	private ActiveEntity createBroker(JSONObject brokerJson) {
		Broker broker = new Broker();
		broker.setId(JSONUtils.getJSONString(brokerJson, "id"));
		broker.setPeerId(JSONUtils.getJSONString(brokerJson, "peer"));
		
		return broker;
	}

	private static Peer createPeer(JSONObject peerJson, Grid grid) {
		Peer peer = new Peer();
		peer.setId(JSONUtils.getJSONString(peerJson, "id"));
		peer.setDiscoveryServiceId(JSONUtils.getJSONString(peerJson, "discoveryService"));
		
		JSONArray workersArray = JSONUtils.getJSONArray(peerJson, "workers");
		
		for (int i = 0; i < workersArray.length(); i++) {
			try {
				JSONObject workerJson = (JSONObject) workersArray.get(i);
				Worker worker = createWorker(workerJson);
				peer.addWorker(worker.getId());
				grid.addObject(worker);
			} catch (JSONException e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		return peer;
	}
	
	private static Worker createWorker(JSONObject workerJson) {
		Worker worker = new Worker();
		worker.setId(JSONUtils.getJSONString(workerJson, "id"));
		worker.setCpu(JSONUtils.getJSONDouble(workerJson, "cpu"));
		return worker;
	}
}

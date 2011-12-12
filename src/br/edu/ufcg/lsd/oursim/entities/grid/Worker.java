package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.accounting.WorkAccounting;

public class Worker extends ActiveEntity {

	private double cpu = 1.;
	private String peer;
	private String consumer;
	private String remotePeer;
	private WorkAccounting currentWorkAccounting;
	private List<WorkAccounting> workAccountings = new LinkedList<WorkAccounting>();
	
	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setRemotePeer(String remotePeer) {
		this.remotePeer = remotePeer;
	}

	public String getRemotePeer() {
		return remotePeer;
	}

	public void setCurrentWorkAccounting(WorkAccounting accounting) {
		this.currentWorkAccounting = accounting;
	}

	public WorkAccounting getCurrentWorkAccounting() {
		return currentWorkAccounting;
	}

	public void addWorkAccounting(WorkAccounting workAccounting) {
		workAccountings.add(workAccounting);
	}

	public void setPeer(String peer) {
		this.peer = peer;
	}
	
	public String getPeer() {
		return peer;
	}
	
	public List<WorkAccounting> getWorkAccountings() {
		return workAccountings;
	}

	public void clearWorkAccountings() {
		workAccountings.clear();
	}

	public void setCpu(double cpu) {
		this.cpu = cpu;
	}

	public double getCpu() {
		return cpu;
	}
}

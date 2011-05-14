package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;

public class DiscoveryService extends ActiveEntity {

	private Set<String> peers = new HashSet<String>();
	
	public boolean addPeer(String peer) {
		return peers.add(peer);
	}
	
	public Set<String> getPeers() {
		return peers;
	}
	
}

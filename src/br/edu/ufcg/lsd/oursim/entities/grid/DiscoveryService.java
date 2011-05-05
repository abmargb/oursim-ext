package br.edu.ufcg.lsd.oursim.entities.grid;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;

public class DiscoveryService extends ActiveEntity {

	private Map<String, Peer> peers = new HashMap<String, Peer>();
	
	public void addPeer(Peer peer) {
		peers.put(peer.getId(), peer);
	}
	
	public Peer getPeer(String peerId) {
		return peers.get(peerId);
	}
	
	public Collection<Peer> getPeers() {
		return peers.values();
	}
	
}

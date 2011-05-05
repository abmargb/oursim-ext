package br.edu.ufcg.lsd.oursim.network;

public class BlankNetwork implements Network {

	@Override
	public Long generateDelay() {
		return 0L;
	}

}

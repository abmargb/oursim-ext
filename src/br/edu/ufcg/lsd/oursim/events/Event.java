package br.edu.ufcg.lsd.oursim.events;

import br.edu.ufcg.lsd.oursim.OurSim;

public interface Event extends Comparable<Event>{

	public static final int DEF_PRIORITY = 1;
	public static final int LOWER_PRIORITY = 0;
	public static final int HIGHER_PRIORITY = Integer.MAX_VALUE;

	void process(OurSim ourSim);
	
	Long getTime();
	
	void setTime(Long time);
	
	String getType();
	
	void setType(String type);
	
	Integer getPriority();

}

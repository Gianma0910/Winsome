package utility;

import java.util.LinkedHashSet;

public class GainAndCurators {

	public double gain;
	
	private LinkedHashSet<String> curators;
	
	public GainAndCurators(double gain, LinkedHashSet<String> curators) {
		this.gain = gain;
		
		this.curators = curators;
	}
	
	public LinkedHashSet<String> getCurators(){
		return curators;
	}
	
}

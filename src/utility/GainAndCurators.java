package utility;

import java.util.LinkedHashSet;

/**
 * Container class for couple (gain, curators). A user x is defined curator of another user y
 * if and only if it is either commented or upvoted a post by x recently. 
 * @author Gianmarco
 *
 */
public class GainAndCurators {

	/** Amount of WINCOIN involved. */
	public double gain;
	/** Set of usernames of curators*/
	private LinkedHashSet<String> curators;
	
	/**
	 * Basic constructor.
	 * @param gain Amount of WINCOIN involved.
	 * @param curators Set of usernames of curators.
	 */
	public GainAndCurators(double gain, LinkedHashSet<String> curators) {
		this.gain = gain;
		
		this.curators = curators;
	}
	
	public LinkedHashSet<String> getCurators(){
		return curators;
	}
	
}

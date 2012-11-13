package de.dfki.movethetileremote.communication;

/**
 * Class used to send points between android client and java server. Has to be
 * used, because android uses another point class than java.
 * 
 * @author Christian
 * 
 */
public class TilePoint {

	public int x;
	
	public int y;
	
	public TilePoint() {}
	
	public TilePoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

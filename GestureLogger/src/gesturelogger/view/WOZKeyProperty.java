package gesturelogger.view;

public class WOZKeyProperty {
	/* VARIABLE DECLARATIONS */
	public gesturelogger.model.Key key;
	
	//the position of the container (x1, x2, y1, y2)
	private float left, right, top, bottom;
	
	//the coordinate of the center point
	private float [] center;
	
	
	
	public WOZKeyProperty ( gesturelogger.model.Key key, float left, float bottom, float right, float top ) {
		this.key = key;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.top = top;
		
		center = new float [2];
		center[0] = left + ((right-left)/2);
		center[1] = top + ((bottom-top)/2);
	}
	
	
	public String getLabel () {
		return key.getChar();
	}
	
	public float getTop () {
		return top;
	}
	
	
	public float getBottom () {
		return bottom;
	}
	
	
	public float getLeft () {
		return left;
	}
	
	
	public float getRight () {
		return right;
	}
	
	
	public float[] getCenter () {
		return center;
	}
}

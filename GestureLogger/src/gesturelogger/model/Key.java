package gesturelogger.model;

public class Key {
	/* VARIABLE DECLARATIONS */
	//the label for the character of key
	public String character;
	
	//the width ratio of the key
	int widthRatio; //default = 1
	
	
	
	/**
	 * public constructor
	 * all key has uniform width
	 * @param character
	 */
	public Key ( String character ) {
		this( character, 1 );
	}
	
	
	
	/**
	 * public constructor
	 * each key has unique width
	 * @param character
	 * @param widthRatio
	 */
	public Key ( String character, int widthRatio ) {
		this.character = character;
		this.widthRatio = widthRatio;
	}
	
	
	public String getChar () {
		return character;
	}
	
	
	public int getWidthRatio () {
		return widthRatio;
	}
}

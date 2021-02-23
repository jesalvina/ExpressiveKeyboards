package gesturelogger.model;

import java.util.ArrayList;

public class WOZKeyboard {
	/* VARIABLE DECLARATIONS */
	//array list of keys per row
	public ArrayList <ArrayList<Key>> rows;
	
	
	/**
	 * method generateRow
	 * to form a row of keyboard with default width
	 * @param keys
	 * @return
	 */
	protected ArrayList<Key> generateRow ( String [] keys ) {
		ArrayList<Key> list = new ArrayList<Key> ();
		
		for ( int i=0; i < keys.length; i++ ) {
			list.add( new Key(keys[i]) );
		}
		
		return list;
	}
	
	
	
	/**
	 * method generateRow
	 * to form a row of keyboard with specific width for each key (e.g. SPACE key)
	 * @param keys
	 * @param widthWeights
	 * @return
	 */
	protected ArrayList<Key> generateRow ( String [] keys, int [] widthWeights ) {
		ArrayList<Key> list = new ArrayList<Key> ();
		
		for ( int i=0; i < keys.length; i++ ) {
			list.add( new Key( keys[i], widthWeights[i] ) );
		}
		
		return list;
	}
	
	
	
	/**
	 * method getRows
	 * @return the setup of keyboard per row
	 */
	public ArrayList <ArrayList<Key>> getRows () {
		return rows;
	}
}

package gesturelogger.view;

import java.util.ArrayList;
import java.util.HashMap;

import gesturelogger.model.Key;
import gesturelogger.model.WOZKeyboard;

public class WOZKeyboardProperty {
	/* VARIABLE DECLARATIONS */
	//keyboard model
	private WOZKeyboard keyboard;
	
	//a list of key view's object per row
	private ArrayList <ArrayList<WOZKeyProperty>> rowsView;

	//a hashmap of key view's object
	private HashMap <String,WOZKeyProperty> keysView;
	
	//width of the screen
	public static float WIDTH;
	
	//height of the screen
	public static float HEIGHT;
	
	//horizontal gap between keys
	public static float X_GAP = 5; //default value
	
	//vertical gap between keys
	public static float Y_GAP = 48;
	
	//key width
	public static float KEY_WIDTH = 100; //default value
	
	//key height
	public static float KEY_HEIGHT = 125;
	
	//margin
	private final static float MARGIN = 4;
	
	
	/**
	 * public constructor
	 * @param width
	 * @param height
	 */
	public WOZKeyboardProperty ( WOZKeyboard keyboard, float width, float height ) {
		this.keyboard = keyboard;
		
		WOZKeyboardProperty.WIDTH = width - MARGIN;
		WOZKeyboardProperty.HEIGHT = height - MARGIN;
		
		//adjust width of the key and gap according to the screen width
		X_GAP = WIDTH / 100;						//1%p
		KEY_WIDTH = new Float (WIDTH * 9.1 / 100);	//9.1%p
		
		//generating key views
		rowsView = new ArrayList <ArrayList<WOZKeyProperty>> ();
		keysView = new HashMap<String,WOZKeyProperty> ();
		ArrayList<WOZKeyProperty> tmpRow;
		float left = 0, right = 0, bottom = HEIGHT;
		for ( int i=keyboard.rows.size(); i > 0; i-- ) {
			ArrayList<Key> row = keyboard.rows.get(i-1);
			
			//set the most-left coordinate for each row
			int totalWidth = 0;
			for ( int n=0; n < row.size(); n++ ) {
				totalWidth += row.get(n).getWidthRatio();
			}
			//full row
			if ( totalWidth == 10 ) {
				left = MARGIN;
			}
			else {
				left = ( WIDTH - (( totalWidth * KEY_WIDTH ) + ( (totalWidth-1) * X_GAP )) ) / 2;
			}
			
			//creating the key view's objects per row
			tmpRow = new ArrayList<WOZKeyProperty> ();
			for ( int j=0; j < row.size(); j++ ) {
				right = left + (KEY_WIDTH*row.get(j).getWidthRatio()) + ( X_GAP * (row.get(j).getWidthRatio()-1) );
				
				//create the new key view
				WOZKeyProperty newKey = new WOZKeyProperty( row.get(j), left, bottom, right, bottom-KEY_HEIGHT );
				tmpRow.add( newKey );
				
				//add the new key to the hashmap
				keysView.put( row.get(j).getChar(), newKey);
				
				left = right + X_GAP;
			}
			rowsView.add( tmpRow );
			
			//set the bottom coordinate for the upper row
			bottom -= (KEY_HEIGHT + Y_GAP);
		}
	}
	
	
	public ArrayList <ArrayList<WOZKeyProperty>> getRowsView () {
		return rowsView;
	}
	
	
	public HashMap<String,WOZKeyProperty> getKeysView () {
		return keysView;
	}
}

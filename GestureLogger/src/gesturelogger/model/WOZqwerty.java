package gesturelogger.model;

import java.util.ArrayList;

public class WOZqwerty extends WOZKeyboard {
	public WOZqwerty () {
		super.rows = new ArrayList<ArrayList<Key>>();
		
		//build the qwerty keyboard
		String []	row1 = { "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P" },
					row2 = { "A", "S", "D", "F", "G", "H", "J", "K", "L" },
					row3 = { "Z", "X", "C", "V", "B", "N", "M" },
					row4 = { "SPACE" };
		int [] weight_row4 = { 5 }; //the size of SPACE is 5 times of the default keys
		
		super.rows.add( generateRow( row1 ) );
		super.rows.add( generateRow( row2 ) );
		super.rows.add( generateRow( row3 ) );
		super.rows.add( generateRow( row4, weight_row4 ) );
	}
}

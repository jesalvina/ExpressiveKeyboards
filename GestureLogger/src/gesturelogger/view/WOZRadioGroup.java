package gesturelogger.view;

import android.graphics.Paint;
import android.util.Log;

public class WOZRadioGroup {
	private String text[];
	private int totalGroup;
	private int totalRadioButton;
	private WOZRadioButtonProperty radioGroups [][];
	private boolean isPicked[];
	private Paint textPaint;
	
	
	
	/**
	 * public constructor
	 * @param totalGroup
	 * @param totalRadioButton
	 * @param Y
	 * @param width
	 */
	public WOZRadioGroup ( String text[], int totalGroup, int totalRadioButton, float Y, float width ) {
		this.text = text;
		this.totalGroup = totalGroup;
		this.totalRadioButton = totalRadioButton;
		
		//set the properties of the text
		textPaint = new Paint();
		textPaint.setTextSize(45);
		textPaint.setTextAlign( Paint.Align.CENTER );
		
		//create radio groups
		this.radioGroups = new WOZRadioButtonProperty [this.totalGroup][this.totalRadioButton];
		this.isPicked = new boolean[] { false, false, false };
		
		float x, y, marginX = width/(totalRadioButton+1);
		int value;
		String id;
		for ( int i=0; i < totalGroup; i++ ) {
			y = Y + (i*180);
			x = marginX*2/3;
			value = -1*(totalRadioButton/2);
			for ( int j=0; j < totalRadioButton; j++ ) {
				//set id: string of (i)(j) e.g. group 0 button 1 is "01"
				id = i + "" + j;
				radioGroups[i][j] = new WOZRadioButtonProperty( id, value, x, y );
				x += marginX;
				value++;
			}
		}
	}
	
	
	public String[] getText () {
		return this.text;
	}
	
	
	public int getTotalGroup () {
		return this.totalGroup;
	}
	
	
	public int getTotalRadioButton () {
		return this.totalRadioButton;
	}
	
	
	public WOZRadioButtonProperty get ( int i, int j ) {
		return this.radioGroups[i][j];
	}
	
	
	public WOZRadioButtonProperty get ( String id ) {
		return this.radioGroups[Character.getNumericValue(id.charAt(0))][Character.getNumericValue(id.charAt(1))];
	}
	
	
	public Paint getTextPaint () {
		return this.textPaint;
	}
	
	
	public String pickRadioButton ( float touchX, float touchY ) {
		Log.v("pick radio group","");
		WOZRadioButtonProperty rb;
		for ( int i=0; i < totalGroup; i++ ) {
			for ( int j=0; j < totalRadioButton; j++ ) {
				rb = radioGroups[i][j];
				if ( rb.LEFT < touchX && rb.RIGHT > touchX && rb.TOP < touchY && rb.BOTTOM > touchY ) {
					Log.v("pick rb",rb.getID());
					rb.pick(true);
					
					//deselect the other radio buttons in the same group
					for ( int k=0; k < totalRadioButton; k++ ) {
						if ( k != j ) {
							radioGroups[i][k].pick(false);
						}
					}
					return rb.getID();
				}
			}
		}
		
		return null;
	}
}

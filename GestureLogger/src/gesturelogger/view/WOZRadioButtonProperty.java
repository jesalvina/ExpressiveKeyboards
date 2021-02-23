package gesturelogger.view;

import android.graphics.Paint;

public class WOZRadioButtonProperty {
	/* VARIABLE DECLARATIONS */
	private int value;
	private String id;
	public float LEFT, RIGHT, TOP, BOTTOM;
	private Paint buttonPaint, buttonRPaint;
	private int buttonColor = 0xFFD3D3D3, buttonRColor = 0xFF333333, pickedColor = 0xFFC8E5E7;
	public boolean isPicked;
	
	
	
	/**
	 * public constructor
	 * @param text
	 * @param top
	 * @param width
	 */
	public WOZRadioButtonProperty ( String id, int value, float left, float top ) {
		this.id = id;
		this.value = value;
		this.TOP = top;
		this.LEFT = left;
		
		initPaint();
		
		//set left and right so the button is on the center of the screen
		this.RIGHT = LEFT+60;
		this.BOTTOM = TOP+60;
		
		isPicked = false;
	}
	
	
	private void initPaint () {
		
		//set the properties of the button's rectangle
		buttonPaint = new Paint();
		buttonPaint.setAntiAlias(true);
		buttonPaint.setColor(buttonColor);
		
		buttonRPaint = new Paint();
		buttonRPaint.setAntiAlias(true);
		buttonRPaint.setStyle(Paint.Style.STROKE);
		buttonRPaint.setColor(buttonRColor);
	}
	
	
	public String getID () {
		return id;
	}
	
	
	public int getValue () {
		return value;
	}


	public Paint getButtonRPaint() {
		return buttonRPaint;
	}


	public Paint getButtonPaint() {
		//set color
		if ( isPicked ) {
			buttonPaint.setColor( pickedColor );
		}
		else {
			buttonPaint.setColor( buttonColor );
		}
		
		return buttonPaint;
	}


	public void pick(boolean pick) {
		isPicked = pick;
	}

}

package gesturelogger.view;

import android.graphics.Paint;
import android.graphics.Rect;

public class WOZButtonProperty {
	/* VARIABLE DECLARATIONS */
	private String text;
	public float LEFT, RIGHT, TOP, BOTTOM, textX, textY;
	private Paint buttonPaint, buttonRPaint, textPaint;
	private int buttonColor = 0xFFD3D3D3, buttonRColor = 0xFF333333, pickedColor = 0xFFC8E5E7;
	public boolean isPicked;
	
	
	
	/**
	 * public constructor
	 * when the button is on the center of the screen
	 * @param text
	 * @param top
	 * @param width
	 */
	public WOZButtonProperty ( String text, float top, float width ) {
		this.text = text;
		this.TOP = top;
		
		initPaint();
		
		//set the coordinate of the rectangle
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds); //to get the text width and height
		
		//set left and right so the button is on the center of the screen
		this.LEFT = (width-(bounds.width()+70))/2;
		this.RIGHT = LEFT+bounds.width()+70;
		this.BOTTOM = top+bounds.height()+70;
		
		this.textX = ( LEFT+(RIGHT-LEFT)/2 );
		this.textY = ( BOTTOM-((BOTTOM-top-bounds.height())/2) );
		
		isPicked = false;
	}
	
	
	
	/**
	 * public constructor
	 * @param text
	 * @param top
	 * @param width
	 */
	public WOZButtonProperty ( String text, float left, float top, float width ) {
		this.text = text;
		this.TOP = top;
		this.LEFT = left;
		
		initPaint();
		
		//set the coordinate of the rectangle
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds); //to get the text width and height
		
		//set left and right so the button is on the center of the screen
		this.RIGHT = LEFT+bounds.width()+50;
		this.BOTTOM = top+bounds.height()+50;
		
		this.textX = ( LEFT+(RIGHT-LEFT)/2 );
		this.textY = ( BOTTOM-((BOTTOM-top-bounds.height())/2) );
		
		isPicked = false;
	}
	
	
	private void initPaint () {
		//set the properties of the text
		textPaint = new Paint();
		textPaint.setTextSize(55);
		textPaint.setTextAlign( Paint.Align.CENTER );
		
		//set the properties of the button's rectangle
		buttonPaint = new Paint();
		buttonPaint.setAntiAlias(true);
		buttonPaint.setColor(buttonColor);
		
		buttonRPaint = new Paint();
		buttonRPaint.setAntiAlias(true);
		buttonRPaint.setStyle(Paint.Style.STROKE);
		buttonRPaint.setColor(buttonRColor);
	}
	
	
	public String getText () {
		return text;
	}


	public Paint getTextPaint() {
		return textPaint;
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


	public void pick() {
		isPicked = true;
	}
}

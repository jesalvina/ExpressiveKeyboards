package gesturelogger.view;

import android.graphics.Color;
import android.graphics.Paint;

public class WOZEmojiProperty {
	/* VARIABLE DECLARATIONS */
	public float LEFT, RIGHT, TOP, BOTTOM, X, Y;
	private Paint circlePaint, circleRPaint, facePaint, mouthPaint;
	private int circleColor = 0xFFFFEE22, circleRColor = 0xFFFFA500;
	
	
	
	/**
	 * public constructor
	 * @param left
	 * @param bottom
	 * @param rad
	 */
	public WOZEmojiProperty ( float left, float bottom, float rad ) {
		this.BOTTOM = bottom;
		this.LEFT = left;
		this.TOP = bottom - (rad*2);
		this.RIGHT = left + (rad*2);
		this.X = left+rad;
		this.Y = this.BOTTOM-rad+10;
		
		initPaint();
	}
	
	
	private void initPaint () {
		//set the properties of the eyes
		facePaint = new Paint();
		facePaint.setAntiAlias(true);
		facePaint.setStyle(Paint.Style.FILL);
		
		mouthPaint = new Paint();
		mouthPaint.setAntiAlias(true);
		mouthPaint.setStyle(Paint.Style.STROKE);
		mouthPaint.setStrokeWidth(5);
		
		//set the properties of the button's rectangle
		circlePaint = new Paint();
		circlePaint.setAntiAlias(true);
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(circleColor);
		
		circleRPaint = new Paint();
		circleRPaint.setAntiAlias(true);
		circleRPaint.setStyle(Paint.Style.STROKE);
		circleRPaint.setStrokeWidth(2);
		circleRPaint.setColor(circleRColor);
	}


	public Paint getFacePaint() {
		return facePaint;
	}


	public Paint getMouthPaint() {
		return mouthPaint;
	}


	public Paint getCircleRPaint() {
		return circleRPaint;
	}


	public Paint getCirclePaint() {
		return circlePaint;
	}
}

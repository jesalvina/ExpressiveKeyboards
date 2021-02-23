package gesturelogger.view;

import java.util.ArrayList;
import java.util.Random;

import gesturelogger.model.CharacterPath;
import gesturelogger.model.Constant;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.Log;

public class OutputView {
	protected String word;
	protected Paint paint;
	protected Rect bound;
	protected float x = -1, y = -1;
	protected double speed, boundingBoxRatio, stdevAngle, speedRatio, speeds[], normalizedSpeedRatio;
	public int red = 0, green = 0, blue = 0;
	protected int outputType;
	protected double totalAngle;
	protected float width;
	
	//for static output
	protected static int textSize = (Constant.EXPTYPE > 3 ? 100 : 75);
	
	//for dynamic output
	private ArrayList <CharacterPath> dynamicOutput;
	
	
	public OutputView ( String word, int outputType ) {
		this.word = ( outputType == Constant.DYNAMIC_OUTPUT ? word.toLowerCase().replaceAll("[^a-z ]", "") : word );
		this.outputType = outputType;
		
		if ( outputType != Constant.DYNAMIC_OUTPUT ) {
			this.paint = new Paint();
			this.paint.setTextSize(textSize);
			this.paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		}
	}
	
	public OutputView ( String word, int outputType, int r, int g, int b, double boundingBoxRatio, double stdevAngle, double speed, double speeds[], Paint paint ) {
		this(word,outputType);
		
		this.paint = paint;
		
		this.red = r;
		this.green = g;
		this.blue = b;
		
		this.speed = speed;
		this.boundingBoxRatio = ( Double.isNaN(boundingBoxRatio) ? 1 : boundingBoxRatio );
		this.stdevAngle = stdevAngle;
		this.speeds = speeds;
		this.speedRatio = ( Double.isNaN(speeds[1]/speeds[0]) ? 1 : speeds[1]/speeds[0] );
		
		//normalize speed ratio: (0,0), (1,1), (2,0)
		getNormalizedSpeedRatio();
	}
	
	
	/**
	 * 
	 * @param boundingBoxRatio
	 * @param stdevAngle
	 * @param speed
	 * @param speeds: first chunk and last chunk
	 * @param totalAngle
	 * @param width
	 */
	public OutputView ( String word, int outputType, double boundingBoxRatio, double stdevAngle, double speed, double speeds[], double totalAngle, float width, int RGBThreshold[] ) {
		this(word,outputType);
		
		this.speed = speed;
		this.boundingBoxRatio = ( Double.isNaN(boundingBoxRatio) ? 1 : boundingBoxRatio );
		this.stdevAngle = stdevAngle;
		this.speeds = speeds;
		this.speedRatio = ( Double.isNaN(speeds[1]/speeds[0]) ? 1 : speeds[1]/speeds[0] );
		
		this.totalAngle = totalAngle;
		this.width = width;
		this.calculateRGB( RGBThreshold );
		
		//normalize speed ratio: (0,0), (1,1), (2,0)
		getNormalizedSpeedRatio();
	}
	
	
	public OutputView ( String word, int outputType, int r, int g, int b, double boundingboxRatio, double speedRatio, float width ) {
		this(word,outputType);
		
		this.red = r;
        this.green = g;
        this.blue = b;
        this.boundingBoxRatio = boundingboxRatio;
        this.speedRatio = speedRatio;
        getNormalizedSpeedRatio();

        this.paint = this.generatePaint( red, green, blue, speedRatio, width );

    } 
	
	
	public void setWord ( String w ) {
		this.word = w;
	}
	
	
	public String getWord () {
		return word;
	}
	
	
	public void setOutputType ( int type ) {
		outputType = type;
	}
	
	public int getOutputType () {
		return outputType;
	}
	
	
	public Paint getPaint () {
		return paint;
	}
	
	
	public float getX () {
		return x;
	}
	
	
	public void setX ( float x ) {
		this.x = x;
	}
	
	
	public float getY () {
		return y;
	}
	
	
	public void setY ( float y ) {
		this.y = y;
	}
	
	
	public double getSpeed () {
		return this.speed;
	}
	
	
	public double getSpeedRatio () {
		return this.speedRatio;
	}
	
	
	public double getAngularity () {
		return this.stdevAngle;
	}
	
	
	public double getSizeRatio () {
		return this.boundingBoxRatio;
	}
	
	
	public double[] getSpeeds () {
		return this.speeds;
	}
	
	
	public static Paint generatePaint( int r, int g, int b, double speedR, float width ) {
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		
		//draw the word output
		//faster --> bluer
		//slow   --> blacker
		if ( speedR > 1 ) {
			//if it goes faster
			paint.setShader(new LinearGradient(0, 0, width, 0, Color.rgb( r, g, 0 ), Color.rgb( r, g, b), Shader.TileMode.MIRROR));
		}
		//if it goes slower
		else {
			paint.setShader(new LinearGradient(0, 0, width, 0, Color.rgb( r, g, b ), Color.rgb( r, g, 0), Shader.TileMode.MIRROR));
		}
		
		return paint;
	}
	
	
	public Rect getBound () {
		//for static & colored output
		if ( this.outputType != Constant.DYNAMIC_OUTPUT ) {
			bound = new Rect();
			
			paint.getTextBounds(word, 0, word.length(), bound); //to get the text width and height
		}
		
		if ( word.equals( "emoji") ) {
			bound = new Rect ( bound.left, bound.top, bound.left+bound.height(), bound.top+bound.height() );
		}
		
		return bound;
	}
	
	
	public void calculateRGB( int threshold[] ) {
		//map bounding box ratio to RED
		//points: (0,255) --> x < 1 then it is red
		//		  (1,0)   --> x = 1 then it is black
		//		  (2,255) --> x > 1 then it is red
		if ( boundingBoxRatio > 2 ) {
			red = threshold[0];
		}
		else {
			//red = (int) (225*boundingBoxRatio*boundingBoxRatio - 450*boundingBoxRatio + 225);
			//System.out.println("red " + red);
			
			red = this.calculateColorValue( boundingBoxRatio, 0, threshold[0], 1, 0, 2, threshold[0] );
		}
		
		//map straightness and angularity to GREEN
		//straight --> black
		//curvy --> green
		if ( totalAngle != 0 ) {
			if ( stdevAngle > 18 || stdevAngle < 6 ) {
				green = threshold[1];
			}
			//6 --> curvy; 12 --> straight; 18 --> mixed
			else {
				//green = (int) (708.3333 - 141.6666*stdevAngle + 7.0833*stdevAngle*stdevAngle);
				//green = (int) (1020 - 170*stdevAngle + 7.083*stdevAngle*stdevAngle);
				
				//System.out.println("green "+green);
				green = this.calculateColorValue( stdevAngle, 6, threshold[1], 12, 0, 18, threshold[1] );
			}
		}
		else {
			if ( stdevAngle > 10 || stdevAngle < 3 ) {
				green = 0;
			}
			//3 --> straight; 6 --> curve; 10 --> mixed
			else {
				//green = (int) (-318.75 + 191.25*stdevAngle - 15.9375*stdevAngle*stdevAngle);
				
				//System.out.println("green "+green);
				green = this.calculateColorValue( stdevAngle, 3, 0, 6, threshold[1], 10, threshold[1] );
			}
		}
		
		//map speed ratio to blue
		//points: (0.5,255) --> x < 0.5 then it is blue
		//		  (1,0)   --> x = 1 then it is black
		//		  (1.5,255) --> x > 1.5 then it is blue
		//the rest is blue
		if ( this.speedRatio < 0.5 || this.speedRatio > 1.5 ) {
			blue = threshold[2];
		}
		else {
			//blue = (int) (1020*this.speedRatio*this.speedRatio - 2040*this.speedRatio + 1020);
			
			//System.out.println("blue "+blue);
			blue = this.calculateColorValue( speedRatio, 0, threshold[2]*4, 1, 0, 1.5, threshold[2] );
		}
		Log.v("rgb", red + " " + green + " " + blue);
		
		this.paint = this.generatePaint( red, green, blue, speedRatio, width );
	}
	
	
	/**
	 * to calculate the R/G/B value based on quadratic function from 3 points
	 * http://stackoverflow.com/questions/2075013/best-way-to-find-quadratic-regression-curve-in-java
	 * @param x
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @return
	 */
	private int calculateColorValue ( double x, double x1, double y1, double x2, double y2, double x3, double y3 ) {

		double y = ((x-x2) * (x-x3)) / ((x1-x2) * (x1-x3)) * y1 +
			    ((x-x1) * (x-x3)) / ((x2-x1) * (x2-x3)) * y2 +
			    ((x-x1) * (x-x2)) / ((x3-x1) * (x3-x2)) * y3;

		//return ( y < 0 ? 0 : (int)y );
		return (int)y;
	}
	
	
	public double getNormalizedSpeedRatio () {
		//normalize speed ratio: (0,0), (1,1), (2,0)
		this.normalizedSpeedRatio = ( speedRatio > 1.5 || speedRatio < 0.5 ? 0 : ( ((-4)*(speedRatio*speedRatio) + 8*speedRatio - 3) ) );
		return normalizedSpeedRatio;
	}
	
	
	public boolean hasDynamicOutput () {
		return ( dynamicOutput != null );
	}
	
	
	public ArrayList<CharacterPath> getDynamicOutput () {
		return dynamicOutput;
	}
	
	
	public float getStrokeWidth() {
		return (float)( this.getSizeRatio() > 2 ? Constant.STROKE_WIDTH*2 : Constant.STROKE_WIDTH*this.getSizeRatio() );
	}
	
	
	/**
	 * generate dynamic output (character-path) without normalizing the speed ratio over a phrase
	 * @param Xmin
	 * @param Ymin
	 * @param scalingFactor
	 * @param width
	 * @return
	 */
	public boolean generateDynamicOutput( int Xmin, int Ymin, double scalingFactor, float width ) {
		dynamicOutput = new ArrayList<CharacterPath>();
		CharacterPath interpolatedCP, otherCP;
		int marginLetter = (int)(50*scalingFactor), leftSpace = 0;
		int top = 10000, bottom = -1, left = 10000, right = -1;	//to calculate bounding box
		int cardinal =  ( scalingFactor > 1 ? 5 : (int)(Math.floor(-4*scalingFactor+9)) );

		//to randomize the position of each control points depending on how curvy the gesture is
		//quadratic function: (0,0), (100,25), (255,50)
		//y = -0.0003x^2 + 0.2848x + 0
		Random r = new Random();
		int offset, curviness = (int)(-0.0003*(green*scalingFactor*green*scalingFactor) + 0.2848*(green*scalingFactor));
		System.out.println( "random factor "+curviness );
		
		//normalizeSpeedRatio();
		System.out.println( "normalized speed ratio "+this.normalizedSpeedRatio );
		//for each character in a word
		for ( int i=0; i<word.length(); i++ ) {
			otherCP = Constant.FONT.get( word.charAt(i) );
			interpolatedCP = new CharacterPath ( Constant.FONT_BASELINE.get( word.charAt(i) ) );
			for ( int j=0; j < interpolatedCP.getPoints().size(); j++ ) {	//iterate per segments
				for ( int k=0; k < interpolatedCP.getPoints(j).size(); k++ ) { //iterate per control points
					//INTERPOLATE control points
					interpolatedCP.getPoints(j).get(k).x = (int) ( (interpolatedCP.getPoints(j).get(k).x * this.normalizedSpeedRatio) + 
																		  (otherCP.getPoints(j).get(k).x * ( 1-this.normalizedSpeedRatio )) );
					interpolatedCP.getPoints(j).get(k).y = (int) ( (interpolatedCP.getPoints(j).get(k).y * this.normalizedSpeedRatio) + 
																		  (otherCP.getPoints(j).get(k).y * ( 1-this.normalizedSpeedRatio )) );
				}
			}
			
			//RANDOMIZE control points
			if ( curviness > 0 ) {
				for ( int j=0; j < interpolatedCP.getPoints().size(); j++ ) {	//iterate per segments
					for ( int k=0; k < interpolatedCP.getPoints(j).size(); k++ ) { //iterate per control points
						offset = ( r.nextInt( curviness ) );
							
						interpolatedCP.getPoints(j).get(k).x += ( offset * ( r.nextInt(2) - 1 ) );
						interpolatedCP.getPoints(j).get(k).y += ( offset * ( r.nextInt(2) - 1 ) );
					}
				}
			}

			//SCALE control points
			if ( scalingFactor != 1.0 ) {
				scale( interpolatedCP, scalingFactor );
			}
			
			//GENERATE PATH
			interpolatedCP.generatePath( interpolatedCP.nbpoints, cardinal );
			
			//TRANSLATE path
			translatePath ( interpolatedCP, Xmin, Ymin, leftSpace );
			
			//update bounding box
			translate ( interpolatedCP, Xmin, Ymin, leftSpace, scalingFactor );
			for ( int j=0; j < interpolatedCP.getPoints().size(); j++ ) {
				ArrayList <Point> points = interpolatedCP.getPoints(j);
				
				for ( int k=0; k < points.size(); k++ ) {
					//search for left of bounding box (first character)
					if ( i == 0 && left > points.get(k).x ) {
						left = points.get(k).x;
					}
					//search for right of bounding box (last character)
					else if ( i == (word.length()-1) && right < points.get(k).x ) {
						right = points.get(k).x;
					}
					//search for top and bottom of bounding box (all character)
					if ( top > points.get(k).y ) {
						top = points.get(k).y;
					}
					if ( bottom < points.get(k).y ) {
						bottom = points.get(k).y;
					}
				}
			}
			this.bound = new Rect( left, top, right, bottom );
			
			//update spacing
			leftSpace += (interpolatedCP.getBoundingBox().width() + marginLetter);
			
			//if the output is not fitted in the screen anymore, break the loop and announce it
			if ( this.getBound().right+50 > width ) {
				return false;
			}
			else {
				dynamicOutput.add( interpolatedCP );
			}
		}
		
		return true;
	}
	
	
	/**
	 * to translate the control points of a character-path
	 * @param cp
	 * @param Xmin
	 * @param Ymin
	 * @param leftPadding
	 * @param scale ONLY IF done after scaling
	 */
	private void translate ( CharacterPath cp, int Xmin, int Ymin, int leftPadding, double scale ) {
		ArrayList<Point> points;
		
		//for each character path, calculate the d(x,y) to the new position
		Point d = new Point ( Xmin - cp.getBoundingBox().left, Ymin - cp.getBoundingBox().top );
		
		//translate all points
		for ( int j=0; j < cp.getPoints().size(); j++ ) {
			points = cp.getPoints(j);
			
			for ( int k=0; k < points.size(); k++ ) {
				points.get(k).x += d.x + leftPadding;
				points.get(k).y += d.y;
			}
		}
		
		//update letter's bounding box
		cp.getBoundingBox().top = Ymin;
		cp.getBoundingBox().bottom = cp.getBoundingBox().top + (int)(Constant.FONT_HEIGHT*scale);
	}
	
	
	/**
	 * to translate the path of a character-path
	 * @param cp
	 * @param Xmin
	 * @param Ymin
	 * @param leftPadding
	 */
	private void translatePath ( CharacterPath cp, int Xmin, int Ymin, int leftPadding ) {
		//calculate the d(x,y) to the new position
		Matrix translateMatrix = new Matrix();
		translateMatrix.setTranslate( Xmin - cp.getBoundingBox().left + leftPadding, Ymin - cp.getBoundingBox().top );
		
		//translate all points
		for ( int j=0; j < cp.getPath().length; j++ ) {
			cp.getPath(j).transform(translateMatrix);
		}
	}
	
	
	/**
	 * to scale the control points of a character-path
	 * @param cp
	 * @param scalingFactor
	 */
	private void scale ( CharacterPath cp, double scalingFactor ) {
		ArrayList<Point> points;
		
		//calculate the translation-value
		//d = R - R'
		Point d = new Point ( (int)(cp.getBoundingBox().left - (cp.getBoundingBox().left*scalingFactor)), 
						(int)(cp.getBoundingBox().top - (cp.getBoundingBox().top*scalingFactor)) );
		
		for ( int j=0; j < cp.getPoints().size(); j++ ) {
			points = cp.getPoints(j);
			
			for ( int k=0; k < points.size(); k++ ) {
				//R' = s.R
				points.get(k).x *= scalingFactor;
				points.get(k).y *= scalingFactor;
				
				//R'' = R' + d
				points.get(k).x += d.x;
				points.get(k).y += d.y;
			}
		}
		
		//update letter's bounding box
		cp.getBoundingBox().bottom = cp.getBoundingBox().top + ((int)(Constant.FONT_HEIGHT*scalingFactor));
	}
}

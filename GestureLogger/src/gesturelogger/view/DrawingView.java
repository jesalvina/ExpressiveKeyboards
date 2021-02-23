package gesturelogger.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import gesturelogger.controller.MainActivity;
import gesturelogger.model.CharacterPath;
import gesturelogger.model.Constant;
import gesturelogger.model.WOZKeyboard;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public abstract class DrawingView extends View {
	/* VARIABLE DECLARATIONS */
	//parent controller: main activity which controls everything
	protected MainActivity parentController;
	//drawing path
	protected Path drawPath;
	//drawing and canvas paint
	protected Paint pathPaint, rectPaint, keyPaint, textPaint, softtextPaint, idPaint, wordPaint, linePaint, ctrpointPaint, fontPaint;
	//normal text size
	protected int textSize = ( Constant.EXPTYPE == 1 ? 65 : 60 );
	//initial color
	protected int pathColor = 0x990EBFE9, feedbackColor[] = new int[] {0x99BDBDBD, 0x66BDBDBD, 0x33BDBDBD}, keyColor = 0xFF444747;
	//view dimension
	//protected float width, height;
	//show/hide keyboard
	protected boolean isKeyboardShown;
	//keyboard type
	protected WOZKeyboard keyboard;
	//keyboard view containing all the keys and setup of the keyboard
	protected WOZKeyboardProperty keyboardView;
	//the y coordinate of the top part of keyboard
	protected float keyboardTop;
	//list of buttons
	protected ArrayList <WOZButtonProperty> buttons;
	protected int pickedButtonIndex = -1;
	//list of radio buttons
	protected WOZRadioGroup radioGroups;
	protected String pickedRadiobuttonID[] = new String[] {"","",""};
	
	//spatial feature: size (ratio of bounding box)
	protected float boundingBoxGesture [] = { Float.MAX_VALUE, 0, Float.MAX_VALUE, 0 };
	protected double gestureArea;
	
	//spatial feature: angularity (mean angle)
	protected double x0, y0, x1, y1, x2, y2;
	protected int threepointsCounter;
	protected ArrayList<Double> thetas;
	protected double totalAngle;
	
	//temporal feature: ratio of speed
	protected double t0, dt, totalTime[];
	protected int twopointsCounter;
	protected double totalSpeed;
	protected ArrayList<Double> speeds;
	
	//word output
	protected ArrayList <OutputView> outputs;		//store the word-outputs as a buffer before forming the phrase-output (combination of several word-outputs) 
	protected static OutputView phraseOutput;		//a temporal field to store the on-going phrase-output
	protected ArrayList <OutputView> allOutputs;	//store the phrase-outputs
	protected EditText outputDisplay;					//the text field
	protected String output;
	protected Button sendButton;						//a dynamic send button
	protected double fontScale = 0.3;
	
	//to record falsity checker during typing activity: whether it's retyped because it's a wrong word or it's a wrong output properties
	protected int currentWordIndex = -1;
	protected int[] totalWrongWords;					//incremented when output != word
	protected int[] totalWrongOutputProperties;			//incremented when output == word but deleted anyway
	
	//Find the directory for the SD Card using the API
	static File sdcard = Environment.getExternalStorageDirectory();
	static BufferedReader br;
	static File file;
	
	
	
	/**
	 * public constructor
	 * @param context
	 * @param attrs
	 */
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		parentController = (MainActivity) context;
		
		initDrawing();
		
		invalidate();
		
		isKeyboardShown = false;
		
		//setVerticalScrollBarEnabled(true);
	}


	/**
	 * method onSizeChanged
	 * override, called when the custom View is assigned a size
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		//whenever the view size changes, re-assign width and height
		//then re-initiate the keyboard view
		initKeyboard( keyboard );
	}
	
	
	/**
	 * method initDrawing
	 * to initialize the path and the paints for both path and canvas
	 */
	private void initDrawing () {
		//create a list of buttons
		buttons = new ArrayList <WOZButtonProperty> ();
		
		//create the path object
		drawPath = new Path();
		
		//set the properties of path
		pathPaint = new Paint();
		pathPaint.setColor(pathColor);
		pathPaint.setAntiAlias(true);
		pathPaint.setStrokeWidth(15);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setStrokeJoin(Paint.Join.ROUND);
		pathPaint.setStrokeCap(Paint.Cap.ROUND);
		
		//set the properties of the text
		textPaint = new Paint();
		textPaint.setTextSize(textSize);
		textPaint.setTextAlign( Paint.Align.CENTER );
		
		//set the properties of the text
		softtextPaint = new Paint();
		softtextPaint.setTextSize(textSize);
		softtextPaint.setTextAlign( Paint.Align.CENTER );
		softtextPaint.setColor(feedbackColor[0]);
		
		//set the properties of key rectangle
		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Paint.Style.STROKE);
		rectPaint.setStrokeWidth(3);
		rectPaint.setColor(keyColor);
		keyPaint = new Paint();
		keyPaint.setTextSize(textSize);
		keyPaint.setColor(keyColor);
		keyPaint.setTextAlign( Paint.Align.CENTER );
		
		//set the properties of the trial ID text
		idPaint = new Paint();
		idPaint.setTextSize(40);
		
		//set the properties of the word text
		wordPaint = new Paint();
		wordPaint.setTextAlign( Paint.Align.CENTER );
		if (Constant.EXPTYPE == 1 ) {
			wordPaint.setTextSize(150);
		}
		else {
			wordPaint.setTextSize(65);
		}
		wordPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		
		//set the properties of key rectangle
		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(3);
		linePaint.setColor(feedbackColor[0]);
		linePaint.setTextSize(65);
		
		//set the properties of control point circle
		ctrpointPaint = new Paint();
		ctrpointPaint.setAntiAlias(true);
		ctrpointPaint.setStyle(Paint.Style.FILL);
		ctrpointPaint.setColor(Color.DKGRAY);

		//set the properties of path
		fontPaint = new Paint();
		fontPaint.setAntiAlias(true);
		fontPaint.setStrokeWidth(Constant.STROKE_WIDTH);
		fontPaint.setStyle(Paint.Style.STROKE);
		fontPaint.setStrokeJoin(Paint.Join.ROUND);
		fontPaint.setStrokeCap(Paint.Cap.ROUND);
		
	}
	
	
	public void initKeyboard ( WOZKeyboard keyboard ) {
		this.keyboard = keyboard;
		//generate keyboard view
		//if ( keyboardView == null ) {
		keyboardView = new WOZKeyboardProperty ( keyboard, getWidth(), getHeight() );
	}
	
	
	public void showKeyboard () {
		isKeyboardShown = true;
	}
	
	
	public void hideKeyboard () {
		isKeyboardShown = false;
	}
	
		
	protected void drawKeyboardView ( Canvas canvas ) {
		//get all the coordinates of the keys from keyboardView
		ArrayList <ArrayList<WOZKeyProperty>> rowsView = keyboardView.getRowsView();
		//then draw
		ArrayList<WOZKeyProperty> row;
		WOZKeyProperty k;
		float top=0;
		for ( int i = 0; i < rowsView.size(); i++ ) {
			row = rowsView.get(i);
			
			for ( int j = 0; j < row.size(); j++ ) {
				k = row.get(j);
				drawKeyView( canvas, k.getLabel(), k.getLeft(), k.getTop(), k.getRight(), k.getBottom());
				
				if ( i == rowsView.size()-1 && j == row.size()-1 ) {
					top = k.getTop() - (WOZKeyboardProperty.Y_GAP/2);
				}
			}
		}
		
		//draw a background for the keyboard
		canvas.drawRect( -5, top, this.getWidth()+5, this.getHeight()+5, rectPaint );
		keyboardTop = top;
	}
	
	
	private void drawKeyView ( Canvas canvas, String keyname, float left, float top, float right, float bottom ) {
		//draw the rectangle
		canvas.drawRect( left, top, right, bottom, rectPaint );
		
		//draw the key
		Rect bounds = new Rect();
		textPaint.getTextBounds(keyname, 0, keyname.length(), bounds); //to get the text width and height
		
		canvas.drawText(keyname, ( left+(right-left)/2 ), ( bottom-((bottom-top-bounds.height())/2) ), keyPaint );
	}
	
	
	
	/**
	 * method drawText
	 * to draw a text on the center of the screen at a specified vertical position
	 * @param text
	 * @param y
	 */
	protected void drawText ( Canvas canvas, String text[], float y ) {
		for ( int i=1; i <= text.length; i++ ) {
			if ( text[i-1].equalsIgnoreCase("accurately") || text[i-1].equalsIgnoreCase("quickly") || text[i-1].equalsIgnoreCase("creatively") ||
					text[i-1].equalsIgnoreCase("three times")) {
				textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
				canvas.drawText(text[i-1], (this.getWidth()/2), y+((textSize+10)*i), textPaint);
				textPaint.setTypeface(Typeface.DEFAULT);
			}
			else {
				canvas.drawText(text[i-1], (this.getWidth()/2), y+((textSize+10)*i), textPaint);
			}
		}
	}
	
	
	
	/**
	 * method drawNote
	 * to draw a text on the left-corner of the screen with smaller size as a note
	 * @param text
	 * @param y
	 */
	protected void drawNote ( Canvas canvas, String text ) {
		textPaint.setColor(Color.BLUE);
		textPaint.setTextAlign(Align.LEFT);
		canvas.drawText(text, 30, keyboardTop-80, textPaint);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextAlign(Align.CENTER);
	}
	
	
	
	/**
	 * method drawInstruction
	 * to draw the instruction layout on the center of the screen at a specified vertical position
	 * @param highlighted	an array of 3 representing which instruction is highlighted
	 * @param y
	 */
	protected void drawInstruction ( Canvas canvas, boolean highlighted[], float y ) {
		float 	xgap = 15, ygap = 25,
				lengthOfLine = (this.getWidth() - (4*xgap))/3;
		
		float left;
		for ( int i=1; i <= 3; i++ ) {
			left = xgap*i + lengthOfLine*(i-1);
			canvas.drawLine( left, y, left+lengthOfLine, y, linePaint);
			
			//draw the text
			switch ( i ) {
				case 1: //accurately
					canvas.drawText("accurately", left+(lengthOfLine/2), y+textSize+(ygap/2), ( highlighted[i-1] ? textPaint : softtextPaint ));
					break;
				case 2: //quickly
					canvas.drawText("quickly", left+(lengthOfLine/2), y+textSize+(ygap/2), ( highlighted[i-1] ? textPaint : softtextPaint ));
					break;
				case 3: //creatively
					canvas.drawText("creatively", left+(lengthOfLine/2), y+textSize+(ygap/2), ( highlighted[i-1] ? textPaint : softtextPaint ));
					break;
			}
		}
		for ( int i=1; i <= 3; i++ ) {
			left = xgap*i + lengthOfLine*(i-1);
			canvas.drawLine( left, y+textSize+(ygap*2), left+lengthOfLine, y+textSize+(ygap*2), linePaint);
		}
	}
	
	
	
	/**
	 * method drawIntertrial
	 * to draw a text on the center of the screen at a specified vertical position
	 * @param text
	 * @param y
	 */
	protected void drawIntertrial ( Canvas canvas, String text[], String buttonText ) {
		//draw the trial ID
		canvas.drawText(text[0], 10, 50, idPaint);
		
		//draw the message
		drawText( canvas, Arrays.copyOfRange(text, 1, text.length), 300 );
		
		//draw the buttons
		drawButton( buttonText, this.getWidth()-300 );
	}
	
	
	
	/**
	 * method drawTrial
	 * to draw a text on the center of the screen at a specified vertical position
	 * @param text
	 * @param y
	 */
	protected void drawTrial ( Canvas canvas, String text[] ) {
		//draw the trial ID
		canvas.drawText(text[0], 10, 50, idPaint);
		
		switch ( Constant.EXPTYPE ) {
		case 1:
			//draw the instruction
			boolean h[] = { false, false, false };
			if ( text[1].equalsIgnoreCase("accurately") ) {
				h[0] = true;
			}
			else if ( text[1].equalsIgnoreCase("quickly") ) {
				h[1] = true;
			}
			else if ( text[1].equalsIgnoreCase("creatively") ) {
				h[2] = true;
			}
			drawInstruction( canvas, h, 130 );
			
			//draw the word
			canvas.drawText(text[2], (this.getHeight()/2), 530, wordPaint);
			break;
			
		case 2:
			//draw the instruction
			textPaint.setTextSize(55);
			if (  text[1].contains(";")) {
				String tmp[] = text[1].split(";");
				canvas.drawText(tmp[0], (this.getWidth()/2), 80, textPaint);
				canvas.drawText(tmp[1], (this.getWidth()/2), 130, textPaint);
			}
			else {
				canvas.drawText(text[1], (this.getWidth()/2), 100, textPaint);
			}
			//draw the word
			canvas.drawText(text[2], (this.getWidth()/2), 220, wordPaint);
			//draw three lines
			canvas.drawLine( 0, 250, this.getWidth(), 250, linePaint);
			canvas.drawText("1", this.getWidth()-50, 330, linePaint);
			canvas.drawLine( 0, 360, this.getWidth(), 360, linePaint);
			canvas.drawText("2", this.getWidth()-50, 440, linePaint);
			canvas.drawLine( 0, 470, this.getWidth(), 470, linePaint);
			canvas.drawText("3", this.getWidth()-50, 550, linePaint);
			canvas.drawLine( 0, 580, this.getWidth(), 580, linePaint);
			
			textPaint.setTextSize(textSize);
			break;
			
		case 3:
			//draw the phrase
			String tmp[] = text[1].split(" "), phrase = "", txt = new String(text[1]);
			int y=110;
			Rect bound;
			for ( int i=0; i < tmp.length; i++ ) {
				//calculate the bounding box of the 
				bound = new Rect();
				textPaint.getTextBounds((phrase+tmp[i]), 0, (phrase+tmp[i]).length(), bound); //to get the text width and height

				if ( (bound.right+100) > this.getWidth() ) {
					canvas.drawText(phrase, (this.getWidth()/2), y, textPaint);
					
					//update for the next line
					y += (bound.height()+5);
					txt = txt.replaceAll(phrase+" ", "");
					phrase = tmp[i];
				}
				else if ( i != 0  ) {
					phrase += (" "+tmp[i]);
				}
				else {
					phrase += tmp[i];
				}
				
				//draw the remaining text, if any
				if ( i == tmp.length-1 && phrase.equals(txt) ) {
					canvas.drawText(phrase, (this.getWidth()/2), y, textPaint);
				}
			}

			//draw a line
			canvas.drawLine( 0, 220, this.getWidth(), 220, linePaint);
			break;
		}
	}
	
	
	
	/**
	 * method drawPosttrial
	 * to draw a text on the center of the screen at a specified vertical position
	 * @param text
	 * @param y
	 */
	protected void drawPosttrial ( Canvas canvas, String text[], String buttonTexts[] ) {
		//draw the trial ID
		canvas.drawText(text[0], 10, 50, idPaint);
		
		switch ( Constant.EXPTYPE ) {
		case 1:
			//draw the question
			drawText( canvas, Arrays.copyOfRange(text, 1, text.length-1), 200 );
			
			//draw the next button
			drawButton( buttonTexts[0], this.getWidth()*2/3, this.getHeight()-300 );
			//draw the trial counter
			canvas.drawText(text[text.length-1], this.getWidth()*2/3, this.getHeight()-80, idPaint);
			
			//draw the buttons
			for ( int i=1; i < buttonTexts.length; i++ ) {
				drawButton( buttonTexts[i], 550+(150*i) );
			}
			
			break;
		case 2:
			//draw the question
			drawText( canvas, Arrays.copyOfRange(text, 1, text.length-1), 150 );
			
			//draw the next button
			drawButton( buttonTexts[0], this.getWidth()*2/3, this.getHeight()-250 );
			//draw the trial counter
			canvas.drawText(text[text.length-1], this.getWidth()*2/3, this.getHeight()-100, idPaint);
			
			//draw the buttons
			drawRadioGroup( Constant.NBREPETITIONS, 5, 700, new String[] {"Strongly disagree", "Strongly agree"} );
			
			break;
			
		case 3:
			//draw the question
			drawText( canvas, Arrays.copyOfRange(text, 1, text.length-1), 150 );
			
			//draw the next button
			drawButton( buttonTexts[0], this.getWidth()*2/3, this.getHeight()-250 );
			//draw the trial counter
			canvas.drawText(text[text.length-1], this.getWidth()*2/3, this.getHeight()-100, idPaint);
			
			break;
		}
	}
	
	
	/**
	 * 
	 * @param canvas
	 * @param text
	 */
	protected void drawQuiz ( Canvas canvas, String text[] ) {
		//draw the instruction
		if (  text[1].contains(";")) {
			String tmp[] = text[1].split(";");
			canvas.drawText(tmp[0], (this.getWidth()/2), 80, textPaint);
			canvas.drawText(tmp[1], (this.getWidth()/2), 130, textPaint);
		}
		else {
			canvas.drawText(text[1], (this.getWidth()/2), 100, textPaint);
		}
		//canvas.drawText(text[2], (this.getWidth()/2), 220, wordPaint);
		
		//draw three lines
		int y = 350;
		for ( int i=0; i<4; i++ ) {
			canvas.drawLine( 0, y, this.getWidth(), y, linePaint);
			
			if ( i != 0 ) {
				canvas.drawText(i+"", this.getWidth()-50, y-20, linePaint);
			}
			
			y+= (int)(Constant.FONT_HEIGHT*fontScale)+5;
		}
	}
	
	
	/**
	 * to draw all rich word output
	 * @param canvas
	 */
	protected void drawOutput ( Canvas canvas, int outputType ) {
		//if we ignore all the feature mapping
		if ( outputType != Constant.DYNAMIC_OUTPUT ) {
			drawOutput( canvas, outputType, 700, 25, 30 );
		}
		else {
			drawOutput( canvas, outputType, 350, 25, 30 );
		}
	}
	
	
	/**
	 * to draw all rich word output
	 * @param canvas
	 */
	protected void drawOutput ( Canvas canvas, int outputType, int Y, int marginX, int marginY ) {
		float y = Y + ( 110*(allOutputs.size()) );
		
		//if there's an on-going phrase being typed
		if ( outputs.size() > 0 ) {
			String phrase = "";
			Paint paint = outputs.get(0).getPaint();
			int r = 0, g = 0, b = 0;
			double meanSpeedRatio[] = new double[] { 0, 0 }, speed = 0, size = 0, angle = 0;
			
			//combine word-outputs into phrase-output ONLY IF it's a rich-output
			for ( int i=0; i < outputs.size(); i++ ) {
				OutputView w = outputs.get(i);
				if ( parentController.isAppliedToAll() ) {
					//update output preference
					w.setOutputType(parentController.getOutputPreference());
					
					//update color
					w.calculateRGB( parentController.getRGBThreshold() );
				}
				
				//build the phrase-output
				phrase += (w.getWord()+" ");
				
				//if the output is text, then calculate the position of each
				if ( w.getOutputType() != Constant.DYNAMIC_OUTPUT ) {
					float x = marginX;
					if ( parentController.isAppliedToAll() || 
							(w.getX() == -1 && w.getY() == -1) ) {
						if ( i > 0 ) {
							x = outputs.get(i-1).getX() + outputs.get(i-1).getBound().width() + marginX;
							if ( (x+w.getBound().width()) > (parentController.width-marginX) ) {
								x = marginX;
								y = outputs.get(i-1).getY() + outputs.get(i-1).getBound().height() + marginY;
							}
							else {
								y = outputs.get(i-1).getY();
							}
						}
						
						w.setX(x);
						w.setY(y);
					}
				}
				
				//for rich-output, then calculate the feature
				r += w.red;
				g += w.green;
				b += w.blue;
				meanSpeedRatio[0] += w.getSpeeds()[0];
				meanSpeedRatio[1] += w.getSpeeds()[1];
				speed += w.getSpeed();
				size += w.getSizeRatio();
				angle += w.getAngularity();
				
				meanSpeedRatio[0] /= outputs.size();
				meanSpeedRatio[1] /= outputs.size();
				
				//create the phrase-output object
				phraseOutput = new OutputView( phrase, outputType, r/outputs.size(), g/outputs.size(), b/outputs.size(), 
							size/outputs.size(), angle/outputs.size(), speed/outputs.size(), meanSpeedRatio,
							OutputView.generatePaint(r/outputs.size(), g/outputs.size(), b/outputs.size(), meanSpeedRatio[1]/meanSpeedRatio[0], this.getWidth()) );
				
				paint = phraseOutput.getPaint();
			}

			//draw on-going output
			switch ( Constant.EXPTYPE ) {
			case 1: case 2:
				canvas.drawText( phrase, outputs.get(0).getX(), outputs.get(0).getY(), paint );
				break;
			case 3: case 30: case 4:
				OutputView w;
				for ( int i=0; i<outputs.size(); i++ ) {
					w = outputs.get(i);
				
					if ( w.getOutputType() != Constant.DYNAMIC_OUTPUT ) {
						if ( w.getWord().equals("emoji") ) {
							this.drawEmoji( canvas, w.getX(), w.getY(), 
												w.getBound().height()/2, w);
						}
						else {
							//if it's static output then just set it to black
							Paint p = new Paint(w.getPaint());
							if ( w.getOutputType() == Constant.STATIC_OUTPUT ) {
								p.setShader(null);
								p.setColor(Color.BLACK);
							}
							
							canvas.drawText( w.getWord(), w.getX(), (w.getY()+w.getPaint().getTextSize()), p );
						}
					}
					
					else {
						CharacterPath cp;
						int marginWord = 150;
	
						//generate word-path if it's not generated yet
						if ( !w.hasDynamicOutput() ) {
							//int left = ( i==0 ? marginX : outputs.get(i-1).getBound().right+((int)(marginWord*fontScale)) );
							int left = ( i==0 ? marginX : (int)outputs.get(i-1).getX()+outputs.get(i-1).getBound().width()+((int)(marginWord*fontScale)) );
							int top = ( i==0 ? Y : (int)outputs.get(i-1).getY() );
							w.setX(left);
							w.setY(top);
							
							boolean successful = w.generateDynamicOutput( left, top, fontScale, this.getWidth() );
							if ( !successful ) {
								left = marginX;
								top += (int)(Constant.FONT_HEIGHT*fontScale)+marginY*fontScale;
								w.generateDynamicOutput( left, top, fontScale, this.getWidth() );
								
								w.setX(left);
								w.setY(top);
							}
	
							System.out.println( "thickness " + w.getStrokeWidth() );
						}
	
						//draw
						if ( w.getWord().equals("emoji") ) {
							this.drawEmoji( canvas, w.getX(), w.getY()+(float)(Constant.FONT_HEIGHT*fontScale*2/3), 
												w.getBound().height()/2, w);
												//100, w);
						}
						else {
							//for each character
							for( int k=0; k < w.getDynamicOutput().size(); k++ ) {
								//COLOR
								fontPaint.setShader( w.getPaint().getShader() );
								
								//THICKNESS
								fontPaint.setStrokeWidth( w.getStrokeWidth() );
								
								cp = w.getDynamicOutput().get(k);
								//for each character, draw all splines
								for ( int j=0; j < cp.getPoints().size(); j++ ) {
									if ( cp.getPath(j) != null ) {
										//line equation (0,8) and (1,5)
										
										canvas.drawPath( cp.getPath(j), fontPaint );
									}
								}
							}
						}
					}
				}
				
				break;
			}
		}

		//draw previously-saved phrase-output
		for ( int n=0; n < allOutputs.size(); n++ ) {
			switch ( Constant.EXPTYPE ) {
			case 1: case 2: 
				y = Y + ( 110*n );
				canvas.drawText( allOutputs.get(n).getWord(), marginX, y, allOutputs.get(n).getPaint() );
				break;
			case 3:
				CharacterPath cp;
				//for each character
				for( int k=0; k < allOutputs.get(n).getDynamicOutput().size(); k++ ) {
					//COLOR
					fontPaint.setShader( allOutputs.get(n).getPaint().getShader() );
					
					//THICKNESS
					fontPaint.setStrokeWidth( (float)( allOutputs.get(n).getSizeRatio() > 2 ? Constant.STROKE_WIDTH*2 : Constant.STROKE_WIDTH*allOutputs.get(n).getSizeRatio() ) );
					
					cp = allOutputs.get(n).getDynamicOutput().get(k);
					//for each character, draw all splines
					for ( int j=0; j < cp.getPoints().size(); j++ ) {
						if ( cp.getPath(j) != null ) {
							//line equation (0,8) and (1,5)
							
							canvas.drawPath( cp.getPath(j), fontPaint );
							//canvas.drawRect(cp.getBoundingBox(), rectPaint);
						}
					}
				}
				break;
			}
		}
		
	}
	
	
	/**
	 * to add the on-going phrase-output to the list
	 * @param canvas
	 */
	protected void addPhraseToOutput () {
		allOutputs.add(phraseOutput);
	}
	
	
	/**
	 * to add the on-going phrase-output to the list
	 * @param canvas
	 */
	protected void addPhraseToOutput ( OutputView w ) {
		allOutputs.add( w );
	}
	
	
	
	/**
	 * method drawButton
	 * to draw a button on the center of the screen at a specified vertical position
	 * @param text
	 * @param y
	 */
	protected void drawButton ( String text, float y ) {
		//create button
		WOZButtonProperty b = new WOZButtonProperty( text, y, this.getWidth() );
		buttons.add ( b );
	}
	
	
	
	/**
	 * method drawButton
	 * to draw a button at a specified position
	 * @param text
	 * @param y
	 */
	protected void drawButton ( String text, float x, float y ) {
		//create button
		WOZButtonProperty b = new WOZButtonProperty( text, x, y, this.getWidth() );
		buttons.add ( b );
	}
	
	
	
	/**
	 * method drawRadioButton
	 * to draw a set of radio button for the post-trial in Experiment 2
	 */
	protected void drawRadioGroup ( int totalGroup, int totalRadioButton, float Y, String text[] ) {
		radioGroups = new WOZRadioGroup( text, totalGroup, totalRadioButton, Y, this.getWidth() );
	}
	
	
	/**
	 * method pickButton
	 * @param x
	 * @param y
	 * @return the button touched
	 */
	protected int pickButton ( float x, float y ) {
		WOZButtonProperty picked = null;
		
		for ( int i=0; i < buttons.size(); i++ ) {
			picked = buttons.get(i); 
			if ( picked.LEFT <= x && picked.RIGHT >= x && picked.TOP <= y && picked.BOTTOM >= y ) {
				picked.pick();
				return i;
			}
		}
		
		return -1;
	}
	
	
	protected void resetButtons () {
		buttons.clear();
		radioGroups = null;
	}
	
	
	protected void drawEmoji ( Canvas canvas, float left, float bottom, float rad, OutputView out ) {
		WOZEmojiProperty em = new WOZEmojiProperty( left, bottom, rad );
		
		canvas.drawCircle( em.X, em.Y, rad, em.getCirclePaint() );
		canvas.drawCircle( em.X, em.Y, rad, em.getCircleRPaint() );
		
		canvas.drawCircle( em.X-(rad*5/12), em.Y-(rad/4), rad/6, em.getFacePaint() );
		canvas.drawCircle( em.X+(rad*5/12), em.Y-(rad/4), rad/6, em.getFacePaint() );
		
		//GREEN to SMILE
		//(255,-10) --> wide smile
		//(120,20)  --> normal smile
		//(0,50)  --> small smile
		//SIZE RATIO to LAUGH
		float startAngle = (float) (0.0001089*out.green*out.green - 0.2631*out.green + 50.00);
		System.out.println("rad "+rad+" arc "+startAngle);
		canvas.drawArc( em.X-(rad*5/12), em.Y-(rad/6), em.X+(rad*5/12), em.Y+(rad/3*2),
				startAngle, 180-(2*startAngle), 
				(out.getSizeRatio() > 1.2 && startAngle < 10 ? true : false), 
				em.getMouthPaint() );
	}
	
	
	/**
	 * method getBoundingBox
	 * to get the bounding box of Y coordinate [minX, maxX, minY, maxY] of a word on the keyboard
	 * @param word
	 * @return float [minX, maxX, minY, maxY]
	 */
	protected float[] getBoundingBox ( String word ) {
		float boundingBox[] = {Float.MAX_VALUE, 0, Float.MAX_VALUE, 0};
		//get all the coordinates of the keys from keyboardView
		HashMap <String,WOZKeyProperty> keysView = keyboardView.getKeysView();
		//then calculate the bounding box one by one letter
		float [] centerPoint;
		for ( int i = 0; i < word.length(); i++ ) {
			centerPoint = keysView.get( Character.toUpperCase(word.charAt(i)) + "" ).getCenter();
			//find the min & max X
			if ( centerPoint[0] < boundingBox[0] ) {
				boundingBox[0] = centerPoint[0];
			}
			if ( centerPoint[0] > boundingBox[1] ) {
				boundingBox[1] = centerPoint[0];
			}
			
			//find the min & max Y
			if ( centerPoint[1] < boundingBox[2] ) {
				boundingBox[2] = centerPoint[1];
			}
			if ( centerPoint[1] > boundingBox[3] ) {
				boundingBox[3] = centerPoint[1];
			}
		}
		return boundingBox;
	}

	
	/**
	 * method calculateBoundingBox
	 * to get the on-going gesture's bounding box on Y-axis
	 * @param touchY
	 */
	protected void calculateBoundingBox(float touchX, float touchY) {
		if ( touchX < boundingBoxGesture[0] ) {
			boundingBoxGesture[0] = touchX;
		}
		if ( touchX > boundingBoxGesture[1] ) {
			boundingBoxGesture[1] = touchX;
		}
		if ( touchY < boundingBoxGesture[2] ) {
			boundingBoxGesture[2] = touchY;
		}
		if ( touchY > boundingBoxGesture[3] ) {
			boundingBoxGesture[3] = touchY;
		}
	}
	
	
	/**
	 * method getBoundingBoxRatio
	 * to compare the bounding box of the on-going gesture with the model
	 * @param word
	 * @return
	 */
	protected double getBoundingBoxRatio( String word ) {
		float[] boundingBoxModel = getBoundingBox(word.replaceAll("[^a-zA-Z]", ""));
		
		//calculate the gesture area
		double modelArea = 0;
		if ( (boundingBoxModel[1]-boundingBoxModel[0]) <= 0 ) {
			//modelArea = (boundingBoxModel[3]-boundingBoxModel[2] ) * 10;
			modelArea = (boundingBoxModel[3]-boundingBoxModel[2] ) * (WOZKeyboardProperty.KEY_WIDTH/4);
		}
		else if ( (boundingBoxModel[3]-boundingBoxModel[2]) <= 0 ) {
			//modelArea = (boundingBoxModel[1]-boundingBoxModel[0]) * 10;
			modelArea = (boundingBoxModel[1]-boundingBoxModel[0]) * (WOZKeyboardProperty.KEY_HEIGHT/4);
		}
		else {
			modelArea = (boundingBoxModel[1]-boundingBoxModel[0]) * (boundingBoxModel[3]-boundingBoxModel[2]);
		}
		
		//calculate the gesture area
		if ( (boundingBoxGesture[1]-boundingBoxGesture[0]) <= 0 ) {
			return (boundingBoxGesture[3]-boundingBoxGesture[2] ) / modelArea;
		}
		else if ( (boundingBoxGesture[3]-boundingBoxModel[2]) <= 0 ) {
			return (boundingBoxGesture[1]-boundingBoxGesture[0]) / modelArea;
		}
		else {
			return (boundingBoxGesture[1]-boundingBoxGesture[0]) * (boundingBoxGesture[3]-boundingBoxGesture[2]) / modelArea;
		}
	}
	
	
	/**
	 * 
	 * http://www.regentsprep.org/regents/math/geometry/gcg6/RCir.htm
	 * http://www.adamfranco.com/2012/12/05/curvature-py/
	 * @param x2
	 * @param y2
	 */
	protected void calculateAngle ( float x3, float y3 )
	{
		if ( x1 == -1 || y1 == -1 ) {
			x1 = x3;
			y1 = y3;
		}
		else if ( x2 == -1 || y2 == -1 ) {
			x2 = x3;
			y2 = y3;
		}
		else {
			//calculate angle
			double dx = x3 - x2;
		    double dy = y3 - y2;
		    double dx1 = x2 - x1;
		    double dy1 = y2 - y1;
		    double theta = Math.abs( Math.atan2(dx*dy1 - dx1*dy, dx*dx1 + dy*dy1)*180/Math.PI ); //Math.Atan2(Cross(A,B), Dot(A,B));
		    //http://stackoverflow.com/questions/21483999/using-atan2-to-find-angle-between-two-vectors
		    	
			//calculate angle
		    if ( theta != 180 ) {
		    	totalAngle += theta;
			    thetas.add(theta);
		    }
		    else {
		    	thetas.add(180-theta);
		    }
	
			//calculate curviness
//			double  mr = (y2-y1)/(x2-x1),
//					mt = (y3-y2)/(x3-x2),
//					middleX = ( (mr*mt*(y3-y1)) + (mr*(x2+x3)) - (mt*(x1+x2)) ) / (2*(mr-mt)),
//					middleY = -1/mr*( middleX - ((x1+x2)/2) ) + ((y1+y2)/2);
//			float r = (float) Math.sqrt( ((x1-middleX)*(x1-middleX)) + ((y1-middleY)*(y1-middleY)) );
//			if ( r != Float.NaN  && r != Float.POSITIVE_INFINITY && r >= 0 ) {
//				totalRadius += r;
//			}
	
		    //update value
		    x1 = x2;
		    y1 = y2;
			x2 = x3;
			y2 = y3;
			threepointsCounter++;	
		}
	}
	
	
	/**
	 * method getStdevAngle
	 * to calculate the standard deviatian of angle, normalized by the number of points-triplet
	 * @return
	 */
	protected double getStdevAngle (String word) {
		double mean = totalAngle / threepointsCounter;
		double temp = 0;
        for( int i=0; i < threepointsCounter; i++) {
            temp += Math.pow((mean-thetas.get(i)),2);
        }
        
       return Math.sqrt(temp/threepointsCounter);
	}
	
	
	/**
	 * 
	 * http://www.regentsprep.org/regents/math/geometry/gcg6/RCir.htm
	 * http://www.adamfranco.com/2012/12/05/curvature-py/
	 * @param x2
	 * @param y2
	 */
	protected void calculateSpeed ( double t1, double x1, double y1 )
	{
		if ( x0 == -1 || y0 == -1 ) {
			t0 = t1;
			x0 = x1;
			y0 = y1;
		}
		else {
			//calculate speed
			double  dt = (t1-t0),
					vx1 = (x1-x0)/dt,
					vy1 = (y1-y0)/dt,
					velocity = Math.sqrt( vx1*vx1 + vy1*vy1 );
			if ( velocity != Double.NaN  && velocity != Double.POSITIVE_INFINITY && velocity >= 0 ) {
				totalSpeed += velocity;
				speeds.add(velocity);
			}
			
		    //update value
		    t0 = t1;
		    x0 = x1;
			y0 = y1;
			twopointsCounter++;
		}
	}
	
	
	/**
	 * method getMeanSpeed
	 * to calculate the mean of speed, normalized by the number of points-pair
	 * @return
	 */
	protected double getMeanSpeed () {
		double mean = totalSpeed / twopointsCounter;
		if ( mean == Double.NaN || mean == Double.POSITIVE_INFINITY || mean < 0 ) {
			mean = 0;
		}
		return mean;
	}
	
	
	protected double getTotalAngle (String word) {
		double angle = 0;
		
		//erase double letter
		ArrayList <Integer> doubleCharIndex = new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder(word.replaceAll("[^a-zA-Z]", ""));
		for ( int i = 0; i < word.replaceAll("[^a-zA-Z]", "").length(); i++ ) {
			for ( int j = i+1; j < word.replaceAll("[^a-zA-Z]", "").length(); j++) {
				if ( sb.charAt(i) == sb.charAt(j) && j == (i+1) ) {
					doubleCharIndex.add(i);
					break;
				}
			}
		}
		int counter = 0;
		for ( int i = 0; i < doubleCharIndex.size(); i++ ) {
			sb.deleteCharAt(doubleCharIndex.get(i)-counter);
			counter++;
		}
		
		//get all the coordinates of the keys from keyboardView
		HashMap <String,WOZKeyProperty> keysView = keyboardView.getKeysView();
		
		//calculate the angle between three point
		float [] centerPoint0, centerPoint1, centerPoint2;
		for ( int i = 0; i < sb.toString().length()-2; i++ ) {
			centerPoint0 = keysView.get( Character.toUpperCase(sb.toString().charAt(i)) + "" ).getCenter();
			centerPoint1 = keysView.get( Character.toUpperCase(sb.toString().charAt(i+1)) + "" ).getCenter();
			centerPoint2 = keysView.get( Character.toUpperCase(sb.toString().charAt(i+2)) + "" ).getCenter();

			//calculate angle
			double dx = centerPoint2[0] - centerPoint1[0];
		    double dy = centerPoint2[1] - centerPoint1[1];
		    double dx1 = centerPoint1[0] - centerPoint0[0];
		    double dy1 = centerPoint1[1] - centerPoint0[1];
		    //double a = Math.abs(Math.atan2(dx*dy1 - dx1*dy, dx*dx1 + dy*dy1)*180/Math.PI);
		    double a = Math.atan2(dx*dy1 - dx1*dy, dx*dx1 + dy*dy1)*180/Math.PI;
		    if ( a == -180 || a == 180 || a == 0 ) {
		    	angle += Math.abs(a);
		    }
		    else {
		    	angle += (180-Math.abs(a));
		    }
		}
		return angle;
	}
	
	
	/**
	 * method getSpeedRatio
	 * to calculate the mean of speed of the first and last chunks of gesture, normalized by the number of points-pair
	 * @return
	 */
	protected double[] getSpeedRatio () {
		double mean[] = {0,0};
		
		//first half
		int x = speeds.size()/2;
		for(int i = 0; i < x; i++){
		    mean[0] += speeds.get(i);
		}
		mean[0] = mean[0] / x;
		
		//last half
		for(int i = x; i < speeds.size(); i++){
		    mean[1] += speeds.get(i);
		}
		mean[1] = mean[1] / (speeds.size()-x);
		
		for ( int i=0; i<2; i++ ) {
			if ( mean[i] == Double.NaN || mean[i] == Double.POSITIVE_INFINITY || mean[i] < 0 ) {
				mean[i] = 1;
			}
		}
		return mean;
	}
	
	
	protected void calculateFeature( StringBuilder text, String word ) {
		Log.v("calculate feature", word);
		double t0 = 0, t1 = 0;
		String [] list = text.toString().split(";");
		for ( int i=0; i < list.length; i++ ) {
			String tmp[] = list[i].split(",");
			float   time = ( tmp[0].isEmpty() || tmp[0] == null ? 0 : Float.parseFloat(tmp[0]) ),
					touchX = ( tmp[1].isEmpty() || tmp[1] == null ? 0 : Float.parseFloat(tmp[1]) ),
					touchY = ( tmp[2].isEmpty() || tmp[2] == null ? 0 : Float.parseFloat(tmp[2]));
			
			//SIZE: calculate bounding box
			calculateBoundingBox( touchX, touchY );
			
			//ANGLE & STRAIGHTNESS: update value
			calculateAngle( touchX, touchY );
			
			//SPEED: start calculating the speed
			calculateSpeed( time, touchX, touchY );
			
			//TIME
			if ( t0 == 0 ) {
				t0 = time;
			}
			t1 = time;
		}
		dt = ( t1 - t0 );
		
		//mark to process and display the output
		Log.v("SIZE", getBoundingBoxRatio(word)+"");
		Log.v("ANGULARITY", getStdevAngle(word)+"");
		Log.v("SPEED_RATIO", getSpeedRatio()[0] + " " + getSpeedRatio()[1] + " " + getSpeedRatio()[1]/getSpeedRatio()[0]);
	}
	
	
	protected void resetFeatureCalculation() {
		boundingBoxGesture[0] = Float.MAX_VALUE;
		boundingBoxGesture[1] = 0;
		boundingBoxGesture[2] = Float.MAX_VALUE;
		boundingBoxGesture[3] = 0;
		
		x0 = y0 = x1 = y1 = x2 = y2 = t0 = -1;
		twopointsCounter = threepointsCounter = 0;
		gestureArea = totalSpeed = totalAngle = 0;
		
		if ( speeds == null ) {
			speeds = new ArrayList<Double>();
		}
		else {
			speeds.clear();
		}
		if ( thetas == null ) {
			thetas = new ArrayList<Double>();
		}
		else {
			thetas.clear();
		}
		
		Log.v(" "," ");
	}
	
	
	/**
	 * to read the written text from the text field, parse them while including the gesture coordinates
	 * @param tag
	 */
	protected void processOutput ( String tag, int outputType ) {
		//add output
		String tmp[] = output.split(" ");
		
		if ( tmp.length > outputs.size() ) {

			resetFeatureCalculation();
	
			//Get the text file
			file = new File(sdcard,"gesture.txt");
	
			//Read text from file
			StringBuilder text = new StringBuilder();
	
			try {
			    br = new BufferedReader(new FileReader(file));
			    String line;
	
			    while ((line = br.readLine()) != null) {
			        text.append(line).append(';');
			        
			        if ( tag != null ) {
			        	parentController.logCoordinate( tag + "," + tmp[tmp.length-1] + "," + line );
			        }
			    }
			    br.close();
			}
			catch (IOException e) {
			    e.printStackTrace();
			}
			calculateFeature( text, tmp[tmp.length-1] );
			addOutput( tmp[tmp.length-1], outputType );
		}
		else if ( outputType == Constant.STATIC_OUTPUT && tmp[tmp.length-1].contains( outputs.get(outputs.size()-1).getWord() ) ) {
			outputs.get(outputs.size()-1).setWord( tmp[tmp.length-1] );
		}
	}

	
	/**
	 * method draw output
	 * to store the rich output text (in Experiment 2) according to the features
	 */
	protected void addOutput( String word, int outputType ) {
		double  boundingBoxRatio = this.getBoundingBoxRatio(word),
				stdevAngle = this.getStdevAngle(word),
				speedRatio [] = this.getSpeedRatio(),
				totalAngle = this.getTotalAngle(word),
				speed = this.getMeanSpeed();
		
		//add output
		outputs.add( new OutputView ( word, outputType, boundingBoxRatio, stdevAngle, speed, speedRatio, totalAngle, getWidth(), parentController.getRGBThreshold() ) );
		
		if ( totalTime != null ) {
			//prepare for the falsity checker
			this.currentWordIndex = ( this.getWordAt(outputs.size()-1) == null ? this.currentWordIndex : outputs.size()-1 );
			totalTime[this.currentWordIndex] += dt;
		}
	}
	
	
	protected void eraseOutput() {
		eraseOutput(false);
	}
	
	
	/**
	 * to erase one word of output
	 */
	protected void eraseOutput ( boolean checkFalsity ) {
		//if the last letter is space, delete it first
		if ( output.length() > 0 && output.charAt( output.length()-1 ) == ' ' ) {
			output = output.substring(0,output.length()-1);
		}
		
		if ( checkFalsity ) {
			//calculate the falsity
			if ( outputs.size() == currentWordIndex ) {
				currentWordIndex--;
			}
			if ( !outputs.isEmpty() && outputs.get( outputs.size()-1 ).getWord().equals( this.getWordAt( this.currentWordIndex ) ) ) {
				this.totalWrongOutputProperties[this.currentWordIndex]++;
			}
			else if ( !outputs.isEmpty() ) {
				this.totalWrongWords[this.currentWordIndex]++;
			}
		}
		
		//when there is only one word
		if ( !output.contains(" ") ) {
			resetOutput();
			outputDisplay.setText("");
		}
		else {
			output = output.substring(0,output.lastIndexOf(" ")) + " ";
			outputDisplay.setText(output);
			if ( outputs.size() > 0 ) {
				outputs.remove(outputs.size()-1);
			}
		}
	}


	/**
	 * to delete on-going phrase-output
	 */
	protected void resetOutput () {
		output = "";
		
		if ( outputs == null ) {
			outputs = new ArrayList<OutputView>();
		}
		else {
			outputs.clear();
		}
		
		currentWordIndex = -1;
	}
	
	
	/**
	 * to delete all phrase-outputs
	 */
	protected void resetAllOutput () {
		resetOutput();
		if ( allOutputs == null ) {
			allOutputs = new ArrayList<OutputView>();
		}
		else {
			allOutputs.clear();
		}
	}
	
	
	/**
	 * 
	 * @return yyyy-MM-dd HH:mm:ss formate date as string
	 */
	protected double getCurrentTime(){
		double time = 0;
	    try {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        String [] tmp = dateFormat.format(new Date()).split(" "); // find today's date
	        time = getMilisecond( tmp[1] );
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }

        return time;
	}
	

	/**
	 * method getMilisecond
	 * to convert time from string format (HH:mm:ss) to miliseconds
	 * @param time
	 * @return
	 */
	public double getMilisecond ( String time ) {
		String tmp[] = time.split(":");
		
		double hour = Double.parseDouble( tmp[0] ) * 3600000; //from hour to mili-second
		double minute = Double.parseDouble( tmp[1] ) * 60000; //from minute to mili-second
		double second = Double.parseDouble( tmp[2] ) * 1000;  //from second to mili-second
		
		return hour+minute+second;
	}

	public abstract void finish();

	public abstract void setExperimentParameter(String word, String instruction, String trialID, int state);
	
	public abstract void setExperimentParameter(String word, String instruction, String trialID, int state, int outputType);

	public abstract String getWordAt( int index );
}

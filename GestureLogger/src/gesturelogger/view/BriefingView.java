package gesturelogger.view;

import gesturelogger.model.CharacterPath;
import gesturelogger.model.Constant;
import gesturelogger.model.Experiment;

import java.util.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;

/**
 * class TutorialView
 * to give a short briefing in Experiment 3 about how the experiment goes
 * @author jalvina
 *
 */
public class BriefingView extends DrawingView {
	/* VARIABLE DECLARATIONS */
	ArrayList <Page> pages;
	private int pageIndex = -1;
	private static TextWatcher textWatcher;
	private static OnClickListener buttonListener;
	
	private boolean isAfterGesture;
	private boolean isDrawingCharacter;
	private CharacterPath charPath;
	private Point ctrPointToChange;
	private Stack<Point> pointStackBuffer;
	private int desiredWidth, desiredHeight;

	public BriefingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initTutorial();
		
		//initialize text watcher to inspect typing activity
		textWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				isAfterGesture = true;
				if ( start == 0 && count == 0 ) {
					resetOutput();
				}
				//adding space only
				else if ( count-before == 1 && s.charAt(s.length()-1) == ' '  ) { 
					if ( output.length() > 1 && output.charAt(output.length()-2) != ' ' ) output += " ";
				}
				//typing
				else if ( count-before == 1 ) { 
					output = s+"";
					String tmp[] = output.split(" ");
					if ( outputs.size() > 0 && output.charAt(output.length()-2) != ' ' ) outputs.remove(outputs.size()-1);
		        	outputs.add( new OutputView ( tmp[tmp.length-1], pages.get(pageIndex).getOutputType() ) );
				}
				else {
					if ( before < count ) {
						output = s+"";
						
						if ( pages.get(pageIndex).hasOutput() ) {
						    new Handler().postDelayed(new Runnable() { 
						         @Override
								public void run() { 
						        	 String tmp[] = output.split(" ");
						        	 outputs.add( new OutputView ( tmp[tmp.length-1], pages.get(pageIndex).getOutputType() ) );
									 invalidate();
						         } 
						    }, 500);
						}
					}
					//if there is a deletion, delete the whole word
					else if ( s.length() < output.length() ) {
						eraseOutput();
					}
				
				}
				outputDisplay.setSelection(outputDisplay.getText().length());
				invalidate();
				
				//scroll
				if ( !outputs.isEmpty() ) {
					ScrollView sv = (ScrollView)getParent();
					sv.smoothScrollTo(0, (int) outputs.get(outputs.size()-1).getY()-300);
				}
			}
	    };
		
	    //initialize button to inspect clicking
	    buttonListener = new View.OnClickListener() {
	        public void onClick(View view) {
	            nextPage();
	            invalidate();
	        }
	    };
	    
		pointStackBuffer = new Stack<Point>();
		desiredWidth = (int)this.getWidth();
		desiredHeight = (int)this.getHeight();
		
		//start the first page of tutorial
		nextPage();
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    int width;
	    int height;
	    //Measure Width
	    if (widthMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        width = widthSize;
	        
	        //store it as the maximum width
	        if ( desiredWidth < widthSize ) {
	        	desiredWidth = widthSize;
	        }
	    } else if (widthMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        width = Math.min(desiredWidth, widthSize);
	    } else {
	        //Be whatever you want
	        width = desiredWidth;
	    }

	    //Measure Height
	    if (heightMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        height = heightSize;
	        
	        //store it as the maximum height
	        if ( desiredHeight < heightSize ) {
	        	desiredHeight = heightSize;
	        }
	    } else if (heightMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        height = Math.min(desiredHeight, heightSize);
	    } else {
	        //Be whatever you want
	        height = desiredHeight;
	    }

	    //report back the measured size to parent class
	    setMeasuredDimension(width, height);
	}
	
	
	private void initTutorial () {
		pages = new ArrayList<Page> ();
		
		pages.add( new Page ("Gesture keyboards let you;"
							+ "write words by drawing a line;"
							+ "that passes through all its letters.;"
							+ " ;"
							+ "Please practice for five minutes.;",
						true,
						"",
						new String[] {"CLEAR","NEXT"},
						Constant.STATIC_OUTPUT
					) );
		
		pages.add( new Page ( "You will be asked;"
							+ "to write text messages;"
							+ "to your friend;"
							+ "using a gesture keyboard.;"
							+ " ;"
							+ "Let's practice.", 
						false,
						new String[] {"NEXT"}
					) );
		
		Page p = new Page();
		p.initInterTrial( "B2T1","" );
		pages.add(p);
		
		p = new Page();
		p.initTrial( "B2T1",
				Constant.STATIC_OUTPUT,
				"the quick brown fox jumps over the lazy dog" );
		pages.add( p );
		
		p = new Page();
		p.initPostTrial( "B2T1","",Constant.NBTRIALS );
		pages.add(p);
		
		pages.add( new Page ( "Now you are ready to start!;"
							+ " ;"
							+ "You will write " + Constant.NBTRIALS + " text messages.;"
							+ "There is a quiz in between.;"
							+ " ;"
							+ "You can take a break;"
							+ "in between the sessions.;",
						false,
						new String[] {"START"}
					) );
	}
	
	
	/**
	 * method onDraw
	 * override, to draw the canvas and all the paths on it
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		//to clear canvas so it's blank
		super.onDraw( canvas );
		resetButtons();
		final Page activePage = pages.get(pageIndex);
		
		//draw a text field to display the keyboard
		if ( activePage.hasKeyboard() && outputDisplay == null ) {
			outputDisplay = parentController.showKeyboard();
			outputDisplay.addTextChangedListener(textWatcher);
		}
		//hide the keyboard and the text field
		else if ( !activePage.hasKeyboard() && outputDisplay != null ) {
			parentController.hideKeyboard( this, outputDisplay );
			outputDisplay = null;
			
			parentController.hideKeyboard( this, sendButton );
			sendButton = null;
		}
		
		//draw the whole page
		if ( activePage.getState() == Experiment.ON_TRIAL ) {
			drawTrial ( canvas, activePage.getText() );

			drawOutput( canvas, Constant.STATIC_OUTPUT, 350, 25, 30 );
			
			if ( sendButton == null ) {
				sendButton = parentController.showButton("SEND");
				sendButton.setOnClickListener(buttonListener);
			}
		}
		else if ( activePage.getState() == Experiment.POST_TRIAL ) {
			drawPosttrial ( canvas, activePage.getText(), activePage.getButtonTexts() );
		}
		else if ( activePage.getState() == Experiment.INTER_TRIAL ) {
			drawIntertrial ( canvas, activePage.getText(), activePage.getButtonText() );
		}
		//for PRACTICE (or ON_BREAK)
		else {
			//set the top position
			int textY = 50;
			if ( !activePage.hasKeyboard() ) {
				textY = 300;
			}
			
			//draw tutorial text
			super.drawText( canvas, activePage.getText(), textY );
			
			//draw output field
			if ( pages.get(pageIndex).hasOutput() ) {
				drawOutput( canvas, Constant.STATIC_OUTPUT );
			}
			
			//draw buttons
			//if there's only one button, draw in the middle
			if ( activePage.getButtonText() != null ) {
				drawButton( activePage.getButtonText(), 900 );
			}
			//if there are more than one, draw at a specific location
			else if ( isAfterGesture && activePage.getButtonTexts() != null ) {
				String tmp[] = activePage.getButtonTexts();
				drawButton( tmp[0], 550, 500 );
				drawButton( tmp[1], 810, 500 );
			}
		}
		
		//draw button on canvas (if any)
		for ( int i=0; i<buttons.size(); i++ ) {
			WOZButtonProperty b = buttons.get(i);
			
			if ( i == pickedButtonIndex ) {
				b.pick();
			}
			
			//draw the rectangle
			canvas.drawRoundRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, 10, 10, b.getButtonPaint() );
			canvas.drawRoundRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, 10, 10, b.getButtonRPaint() );
			
			//draw the text
			canvas.drawText( b.getText(), b.textX, b.textY, b.getTextPaint() );
		}
	}
	
	
	@Override
	/**
	 * method onTouchEvent
	 * to draw a path (without logging it)
	 */
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		
		this.requestFocus();
		
		int b;
		switch (event.getAction()) {
			//when the user touches the View, move to that position to start drawing
			case MotionEvent.ACTION_DOWN:
				//pick buttons
				b = pickButton( touchX, touchY );
				if ( b != -1 ) {
					pickedButtonIndex = b;
				}
				//pick the circle when drawing the character
				//to change the position
				else if ( isDrawingCharacter ) {
					for ( int i=0; i < charPath.getPoints().size(); i++ ) {
						for ( int j=0; j < charPath.getPoints().get(i).size(); j++ ) {
							if ( Math.abs( charPath.getPoints().get(i).get(j).x - touchX ) <= Constant.CONTROLPOINT_RAD  &&
								 Math.abs( charPath.getPoints().get(i).get(j).y - touchY ) <= Constant.CONTROLPOINT_RAD ) {
								ctrPointToChange = charPath.getPoints().get(i).get(j);
								break;
							}
						}
					}
				}
				
			    break;
			    
			//do nothing
			case MotionEvent.ACTION_MOVE:
				//draw the character
				if ( isDrawingCharacter && ctrPointToChange != null ) {
					ctrPointToChange.x = (int) touchX;
					ctrPointToChange.y = (int) touchY;
				}
				break;
			    
			//when they lift their finger up off the View, draw the Path and reset it for the next drawing operation
			case MotionEvent.ACTION_UP:
			    //handle button-picking
				b = pickButton( touchX, touchY );
				if ( b != -1 && pickedButtonIndex == b ) {
					String buttonText = buttons.get(pickedButtonIndex).getText();
					
					//during common pages
					if ( buttonText.equalsIgnoreCase("next") || buttonText.equalsIgnoreCase("done") ) {
						this.nextPage();
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					//during the writing
					else if ( buttonText.equalsIgnoreCase("clear") ) {
						resetOutput();
						outputDisplay.setText("");
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					
					//during the finishing of practice session
					else {
				        //change layout
				        parentController.startExperiment();
					}
				}
				
				//handle the character drawing
				else if ( isDrawingCharacter ) {
					if ( ctrPointToChange != null ) {
						ctrPointToChange = null;
					}
					else {
						isAfterGesture = true;
						charPath.draw(touchX, touchY);
						pointStackBuffer.removeAllElements();
					}
					
				}
				
			    break;
		}
		
		//call onDraw
		invalidate();
		
		return true;
	}


	/**
	 * method nextPage
	 * to page-down the tutorial
	 */
	public void nextPage () {
		if ( pageIndex < (pages.size()-1) ) {
			pageIndex++;
			isAfterGesture = false;
			
			//erase all output
			resetAllOutput();
		}
		
	}


	@Override
	public void setExperimentParameter(String word, String instruction, String trialID, int state) {
		
	}


	@Override
	public void finish() {
		
	}


	@Override
	public void setExperimentParameter(String word, String instruction, String trialID, int state, int outputType) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getWordAt(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}

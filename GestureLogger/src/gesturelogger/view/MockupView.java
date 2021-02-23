package gesturelogger.view;

import gesturelogger.model.Constant;
import gesturelogger.model.Experiment;

import java.util.*;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;

/**
 * class MockupView
 * build an interface for the mock-ups
 * @author jalvina
 *
 */
public class MockupView extends DrawingView {
	/* VARIABLE DECLARATIONS */
	ArrayList <Page> pages;
	private int pageIndex = -1;
	static TextWatcher textWatcher;
	
	private int trialCounter = 1;
	private boolean isAfterGesture;
	
	//to deal with typing
	private StringBuilder ongoingWord;
	
	//to handle scrollview
	private int desiredWidth, desiredHeight;

	public MockupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initTutorial();
		
		//start the first page of tutorial
		nextPage();
		
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
				//typing
				else if ( count-before == 1 ) {
					output = s+"";
					
					char lastChar = s.charAt(s.length()-1);
					//if it's a character, add it to the ongoing word
					//if ( lastChar != ' ' &&  ongoingWord == null ) {
					//	ongoingWord = new StringBuilder( lastChar );
					//}
					//else 
					if ( lastChar != ' ' ) {
						//ongoingWord.append( lastChar );

						processOutput( null, Constant.STATIC_OUTPUT );
					}
					//adding space when there's an ongoing word being typed
					else if ( ongoingWord != null ) {
						//processOutput( null, Constant.STATIC_OUTPUT );
						
						//reset ongoing word
						ongoingWord = null;
					}
				}
				else {
					if ( before < count ) {
						output = s+"";
						
						if ( pages.get(pageIndex).hasOutput() ) {
						    new Handler().postDelayed(new Runnable() { 
						         @Override
								public void run() { 
						        	 processOutput( null, parentController.getOutputPreference() );
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
			}
	    };
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
		
		pages.add( new Page (Constant.DYNAMIC_OUTPUT,
							 new String[] { "CLEAR" }
				) );
		
//		pages.add( new Page (Constant.COLORED_OUTPUT,
//				 new String[] { "SEND" }
//				) );
//
//		pages.add( new Page (Constant.DYNAMIC_OUTPUT,
//						 new String[] { "SEND" }
//				) );
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
		else if ( !activePage.hasKeyboard() && outputDisplay != null ) {
			parentController.hideKeyboard( this, outputDisplay );
			outputDisplay = null;
		}
		
		
		//draw tutorial text
		//super.drawText( canvas, activePage.getText(), 10 );
		
		//draw output field
		drawOutput( canvas, parentController.getOutputPreference(), 240, 45, 60 );
		
		//draw buttons
		if ( activePage.getButtonText() != null ) {
			drawButton( activePage.getButtonText(), 810, 80 );
			//drawButton( activePage.getButtonText(), 810, 600 );
		}
		else if ( isAfterGesture && activePage.getButtonTexts() != null ) {
			String tmp[] = activePage.getButtonTexts();
			drawButton( tmp[0], 550, 100 );
			drawButton( tmp[1], 810, 100 );
		}

		//draw button on canvas (if any)
		for ( int i=0; i<buttons.size(); i++ ) {
			WOZButtonProperty b = buttons.get(i);
			
			if ( i == pickedButtonIndex ) {
				b.pick();
			}
			
//			//draw the rectangle
//			canvas.drawRoundRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, 10, 10, b.getButtonPaint() );
//			canvas.drawRoundRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, 10, 10, b.getButtonRPaint() );
			canvas.drawRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, b.getButtonPaint() );
			canvas.drawRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, b.getButtonRPaint() );
			
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
				
			    break;
			    
			//do nothing
			case MotionEvent.ACTION_MOVE:
				break;
			    
			//when they lift their finger up off the View, draw the Path and reset it for the next drawing operation
			case MotionEvent.ACTION_UP:
			    //handle button-picking
				b = pickButton( touchX, touchY );
				if ( b != -1 && pickedButtonIndex == b ) {
					String buttonText = buttons.get(pickedButtonIndex).getText();
					
					//during common pages
					if ( buttonText.equalsIgnoreCase("next") || buttonText.equalsIgnoreCase("done")  || 
							buttonText.equalsIgnoreCase("send") ) {
						//reset picked button index
						pickedButtonIndex = -1;
						
						//erase output text
						output = "";
						if ( outputDisplay != null ) {
							outputDisplay.setText("");
						}
						
						//next
						this.nextPage();
					}
					else if ( buttonText.equalsIgnoreCase("clear") ) {
						resetOutput();
						outputDisplay.setText("");
						
						//reset picked button index
						pickedButtonIndex = -1;
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
		// TODO Auto-generated method stub
		
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

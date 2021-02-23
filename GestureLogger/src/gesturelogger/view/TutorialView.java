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

/**
 * class TutorialView
 * to give tutorial in Experiment 2 about how to manipulate the gesture and what features users can play with
 * @author jalvina
 *
 */
public class TutorialView extends DrawingView {
	/* VARIABLE DECLARATIONS */
	ArrayList <Page> pages;
	private int pageIndex = -1;
	static TextWatcher textWatcher;
	
	private int trialCounter = 1;
	private boolean isAfterGesture;

	public TutorialView(Context context, AttributeSet attrs) {
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
				//adding space only
				else if ( count-before == 1 && s.charAt(s.length()-1) == ' '  ) { 
					//do nothing
				}
				else {
					if ( before < count ) {
						output = s+"";
						
						if ( pages.get(pageIndex).hasOutput() ) {
						    new Handler().postDelayed(new Runnable() { 
						         @Override
								public void run() { 
						        	 processOutput( null, pages.get(pageIndex).getOutputType() );
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
	
	
	private void initTutorial () {
		pages = new ArrayList<Page> ();
		
		pages.add( new Page ("You can write words by;"
								+ "drawing a line that passes;"
								+ "through all its letters.;"
								+ " ;"
								+ "Draw \"welcome\";",
							true,
							"",
							new String[] {"CLEAR","DONE"},
							Constant.STATIC_OUTPUT
				) );
		
		pages.add( new Page ("Now draw \"welcome\" creatively!;"
								+ "How many colors can you make?;",
							true,
							"welcome",
							new String[] {"CLEAR","DONE"},
							Constant.COLORED_OUTPUT
				) );
		
//		pages.add( new Page ("",
//							true,
//							"welcome",
//							new String[] {"CLEAR","DONE"}
//				) );
		pages.add( new Page ( "You will be asked;"
				+ "to draw a phrase;"
				+ "three times;"
				+ "with a specific goal.;"
				+ " ;"
				+ "Let's practice.", 
			false,
			new String[] {"NEXT"}
				) );
		
		Page p = new Page();
		p.initInterTrial( " ",
							"Try to make each phrase;the same color of green"
				);
		pages.add(p);
		
		p = new Page();
		p.initTrial( " ",
							"good morning",
							"Try to make each phrase;the same color of green"
				);
		pages.add(p);
		
		p = new Page();
		p.initPostTrial( " ",
							" ",
							Constant.NBTRIALS
				);
		pages.add(p);
		
		pages.add( new Page ( "Now you are ready to start!;"
								+ " ;"
								+ "There are two sessions.;"
								+ "In total you will write " + Constant.NBTRIALS + " phrases.;"
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
		else if ( !activePage.hasKeyboard() && outputDisplay != null ) {
			parentController.hideKeyboard( this, outputDisplay );
			outputDisplay = null;
		}
		
		//draw the whole page
		if ( activePage.getState() == Experiment.ON_TRIAL ) {
			drawTrial ( canvas, activePage.getText() );

			drawOutput( canvas, Constant.COLORED_OUTPUT, 330, 28, 55 );
			
			//has drawn "good morning" on a trial
			if ( outputs.size() == 2 ) {
				drawButton( activePage.getButtonText(), 590 );
			}
		}
		else if ( activePage.getState() == Experiment.POST_TRIAL ) {
			drawPosttrial ( canvas, activePage.getText(), activePage.getButtonTexts() );
		}
		else if ( activePage.getState() == Experiment.INTER_TRIAL ) {
			drawIntertrial ( canvas, activePage.getText(), activePage.getButtonText() );
		}
		else {
			//draw instruction layout
			int textY = 50;
			if ( activePage.getInstructions() != null ) {
				super.drawInstruction(canvas, activePage.getInstructions(), ( activePage.hasKeyboard() ? 70 : 570 ));
				textY = 300;
			}
			else if ( !activePage.hasKeyboard() ) {
				textY = 300;
			}
			
			//draw tutorial text
			super.drawText( canvas, activePage.getText(), textY );
			
			//draw output field
			if ( pages.get(pageIndex).hasOutput() ) {
				if ( pages.get(pageIndex).getWord().equals("") ) {
					drawOutput( canvas, Constant.STATIC_OUTPUT );
				}
				else {
					drawOutput( canvas, Constant.COLORED_OUTPUT );
				}
			}
			
			//draw buttons
			if ( activePage.getButtonText() != null ) {
				drawButton( activePage.getButtonText(), 900 );
			}
			else if ( isAfterGesture && activePage.getButtonTexts() != null ) {
				String tmp[] = activePage.getButtonTexts();
				drawButton( tmp[0], 550, 600 );
				drawButton( tmp[1], 810, 600 );
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
		//draw radio button on canvas (if any)
		for ( int i=0; radioGroups != null && i<radioGroups.getTotalGroup(); i++ ) {
			//draw three lines
			canvas.drawLine( 0, radioGroups.get(i,0).TOP-65, this.getWidth(), radioGroups.get(i,0).TOP-65, linePaint);
			canvas.drawText( ""+(i+1), this.getWidth()-80, radioGroups.get(i,0).BOTTOM, linePaint);
			for ( int j=0; j<radioGroups.getTotalRadioButton(); j++ ) {
				WOZRadioButtonProperty b = radioGroups.get(i,j);
				
				if ( pickedRadiobuttonID != null && pickedRadiobuttonID[Character.getNumericValue(b.getID().charAt(0))].equals(b.getID()) ) {
					b.pick(true);
				}
				
				//draw the rectangle
				canvas.drawRoundRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, 10, 10, b.getButtonPaint() );
				canvas.drawRoundRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, 10, 10, b.getButtonRPaint() );
				
				//draw the text
				if ( i == 0 && j == 0 ) {
					canvas.drawText( radioGroups.getText()[0], b.RIGHT, b.TOP-90, radioGroups.getTextPaint() );
				}
				else if ( i == 0 && j == radioGroups.getTotalRadioButton()-1 )
				{
					canvas.drawText( radioGroups.getText()[1], b.RIGHT+10, b.TOP-90, radioGroups.getTextPaint() );
				}
			}
		}
		if ( radioGroups != null ) {
			canvas.drawLine( 0, radioGroups.get(radioGroups.getTotalGroup()-1,0).BOTTOM+65, this.getWidth(), radioGroups.get(radioGroups.getTotalGroup()-1,0).BOTTOM+65, linePaint);
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
				
				//pick radio button
				if ( radioGroups != null ) {
					String tmp = radioGroups.pickRadioButton(touchX, touchY);
					if ( tmp != null ) {
						pickedRadiobuttonID[Character.getNumericValue(tmp.charAt(0))] = tmp;
					}
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
					if ( buttonText.equalsIgnoreCase("next") || buttonText.equalsIgnoreCase("done") ) {
						//if there's answer during the post-trial OR it's not during post-trial
						if ( pages.get(pageIndex).getState() != Experiment.POST_TRIAL || 
								( pickedRadiobuttonID[0] != "" && pickedRadiobuttonID[1] != "" && pickedRadiobuttonID[2] != "" ) ) {
							this.nextPage();
						}
						//reset picked button index
						pickedButtonIndex = -1;
						
						//erase output text
						output = "";
						if ( outputDisplay != null ) {
							outputDisplay.setText("");
						}
					}
					else if ( buttonText.equalsIgnoreCase("clear") ) {
						resetOutput();
						outputDisplay.setText("");
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					else if ( buttonText.equalsIgnoreCase("NEXT PHRASE") ) {
						if ( (trialCounter%Constant.NBREPETITIONS) != 0 ) {
							trialCounter++;
							addPhraseToOutput();
							resetOutput();
							invalidate();
							outputDisplay.setText("");
						}
						else {
							addPhraseToOutput();
							invalidate();
							resetOutput();
							resetAllOutput();
							outputDisplay.setText("");
						    
							this.nextPage();
						}
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					//during the finishing of practice session
					else {
				        //change layout
				        parentController.startExperiment();
					}
					
					//handle radio-button picking
					if ( radioGroups != null ) {
						String tmp = radioGroups.pickRadioButton(touchX, touchY);
						if ( tmp != null && pickedRadiobuttonID[Character.getNumericValue(tmp.charAt(0))].equals(tmp) ) {
							pickedRadiobuttonID[Character.getNumericValue(tmp.charAt(0))] = "";
						}
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

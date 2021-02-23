package gesturelogger.view;

import gesturelogger.model.Constant;
import gesturelogger.model.Experiment;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * class PracticeView
 * to give briefing to users in Experiment 1 about how to gesture-type and about what each instruction means
 * @author jalvina
 *
 */
public class PracticeView extends DrawingView {
	/* VARIABLE DECLARATIONS */
	ArrayList <Page> pages;
	private int pageIndex = -1;
	private String answer;
	
	private boolean isAfterGesture;
	private CountDownTimer timer;

	public PracticeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initTutorial();

		//start the first page of tutorial
		nextPage();
	}
	
	
	private void initTutorial () {
		pages = new ArrayList<Page> ();
		
		pages.add( new Page ("You can write words by;"
								+ "drawing a line that passes;"
								+ "through all its letters.;"
								+ " ;"
								+ "Let's practice.;"
								+ " ;"
								+ "Draw \"hello\";",
							true,
							new String[] {"Practice again","Done"}
				) );
		
		pages.add( new Page ( "You will be asked to;"
								+ "draw each word;"
								+ " ;"
								+ " ;"
								+ " ;"
								+ "Let's practice.", 
							false,
							new String[] {"NEXT"},
							new boolean[] { true, true, true }
				) );
		
		pages.add( new Page ( "Draw \"hello\" as;"
								+ "accurately;"
								+ "as you can.",
							true,
							new String[] {"Practice again","Done"},
							new boolean[] { true, false, false }
				) );
		
		pages.add( new Page ( "Draw \"hello\" as;"
								+ "quickly;"
								+ "as you can;"
								+ "while still being accurate.",
							true,
							new String[] {"Practice again","Done"},
							new boolean[] { false, true, false }
				) );
		
		pages.add( new Page ( "Draw \"hello\" as;"
								+ "creatively;"
								+ "as you can. Have fun!",
							true,
							new String[] {"Practice again","Done"},
							new boolean[] { false, false, true }
				) );
		
		pages.add( new Page ( "Please write each word;"
								+ "according to the instruction.;"
								+ " ;"
								+ "Touch the screen to start the trial,;"
								+ "lift your finger to end the trial.;"
								+ " ;"
								+ "Let's practice.",
							false,
							new String[] {"NEXT"}
				) );
		
		Page p = new Page();
		p.initTrial( " ",
							"world",
							"accurately"
				);
		pages.add(p);
		
		p = new Page();
		p.initPostTrial ( " ",
							"world",
							Constant.NBTRIALS
				);
		pages.add(p);
		
		pages.add( new Page ( "Now you are ready to start!;"
								+ " ;"
								+ "You will write " + Constant.NBTRIALS + " words.;"
								+ " ;"
								+ "You can take a break after;"
								+ "every " + (Constant.NBTRIALS/(Constant.NBWORDS/Experiment.wordsBeforeBreak)) + " words.;",
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
		
		//draw the keyboard and the paths on the canvas
		if ( isKeyboardShown ) {
			drawKeyboardView(canvas);
			
			if ( drawPath != null ) {
				canvas.drawPath(drawPath, pathPaint);
			}
		}
		
		//draw the whole page
		Page activePage = pages.get(pageIndex);
		if ( activePage.getState() == Experiment.ON_TRIAL ) {
			drawTrial ( canvas, activePage.getText() );
		}
		else if ( activePage.getState() == Experiment.POST_TRIAL ) {
			drawPosttrial ( canvas, activePage.getText(), activePage.getButtonTexts() );
		}
		else {
			//draw instruction layout
			int textY = 100;
			if ( activePage.getInstructions() != null ) {
				super.drawInstruction(canvas, activePage.getInstructions(), ( activePage.hasKeyboard() ? 130 : 570 ));
				textY = 350;
			}
			
			//draw tutorial text
			super.drawText( canvas, activePage.getText(), textY );
			
			//draw buttons
			if ( activePage.getButtonText() != null ) {
				drawButton( activePage.getButtonText(), ( activePage.hasKeyboard() ? 750 : this.getHeight()-500 ) );
			}
			else if ( isAfterGesture && activePage.getButtonTexts() != null ) {
				String tmp[] = activePage.getButtonTexts();
				drawButton( tmp[0], 100, keyboardTop-180 );
				drawButton( tmp[1], 800, keyboardTop-180 );
			}
		}
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
		
		int b;
		switch (event.getAction()) {
			//when the user touches the View, move to that position to start drawing
			case MotionEvent.ACTION_DOWN:
				stopTimer();
				
				//pick buttons
				b = pickButton( touchX, touchY );
				if ( b != -1 ) {
					pickedButtonIndex = b;
				}
				
				//draw path
				if ( isKeyboardShown ) {
					drawPath.moveTo(touchX, touchY);
				}
				
			    break;
			    
			//when they move their finger on the View, draw the path along with their touch
			case MotionEvent.ACTION_MOVE:
				if ( isKeyboardShown ) {
					drawPath.lineTo(touchX, touchY);
				}
			    break;
			    
			//when they lift their finger up off the View, draw the Path and reset it for the next drawing operation
			case MotionEvent.ACTION_UP:
			    //reset path in-motion
				if ( isKeyboardShown ) {
					//let the drawn path stays a bit longer on the screen before reseting it
					int i = 0;
					pathPaint.setColor(feedbackColor[i]);
					isAfterGesture = true;
					invalidate();
//					timer = new CountDownTimer(900, 300) {
//						int i = 1;
//						
//						@Override
//						public void onTick(long millisUntilFinished) {
//							//pathPaint.setColor(feedbackColor[i]);
//							//i++;
//							//invalidate();
//						}
//
//						@Override
//						public void onFinish() {
//							timer = null;
//							drawPath.reset();
//							pathPaint.setColor(pathColor);
//							isAfterGesture = true;
//							invalidate();
//						}
//					}.start();
					

					if ( pages.get(pageIndex).getState() == Experiment.ON_TRIAL ) {
					    nextPage();
					}
				}

				//handle button-picking
				b = pickButton( touchX, touchY );
				if ( b != -1 && pickedButtonIndex == b ) {
					String buttonText = buttons.get(pickedButtonIndex).getText();
					
					//during common pages
					if ( pageIndex != pages.size()-1 && (buttonText.equalsIgnoreCase("next") || buttonText.equalsIgnoreCase("done")) ) {
						//if there's answer during the post-trial OR it's not during post-trial
						if ( pages.get(pageIndex).getState() != Experiment.POST_TRIAL || answer != null ) {
							this.nextPage();
						}
						//reset picked button index
						pickedButtonIndex = -1;
					}
					else if ( pageIndex != pages.size()-1 && buttonText.equals("Practice again") ) {
						stopTimer();
						
						//reset picked button index
						pickedButtonIndex = -1;
						
					}
					//during the post-questionnaire
					else if ( pageIndex != pages.size()-1 ) {
						answer = buttonText;
					}
					//during the finishing of practice session
					else {
				        //change layout
				        parentController.startExperiment();
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
			
			if ( pages.get(pageIndex).hasKeyboard() ) {
				showKeyboard();
			}
			else {
				hideKeyboard();
			}
		}
	}
	
	
	public void stopTimer () {
		isAfterGesture = true;
		drawPath.reset();
		pathPaint.setColor(pathColor);
//		if ( timer != null ) {
//			timer.cancel();
//			timer = null;
//		}
	}


	@Override
	public void setExperimentParameter(String word, String instruction, String trialID, int state) {
		// TODO Auto-generated method stub
		
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

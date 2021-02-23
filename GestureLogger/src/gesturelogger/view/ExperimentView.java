package gesturelogger.view;

import gesturelogger.model.Experiment;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ExperimentView extends DrawingView {
	/* VARIABLE DECLARATIONS */
	//the active word
	private String word;
	//the active instruction
	private String instruction;
	//the active trial ID
	private String trialID;
	//the answer of post-questionnaire
	private String answer;
	//the time to finish a gesture
	private double time;
	private long startTime;
	private int trialCounter;
	private Page activePage;

	
	
	public ExperimentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//set default value
		trialCounter = 0;
		trialID = "";
		word = "";
		instruction = "";
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

		//on trial
		switch ( parentController.getState() ) {
			//during trial
			case Experiment.ON_TRIAL:
				drawKeyboardView(canvas);
				drawTrial( canvas, activePage.getText() );
				
				if ( drawPath != null ) {
					canvas.drawPath(drawPath, pathPaint);
				}
				break;
			//after trial: the post-questionnaire
			case Experiment.POST_TRIAL:
				drawPosttrial ( canvas, activePage.getText(), activePage.getButtonTexts() );
				break;
			//before trial (inter trial)
			case Experiment.INTER_TRIAL:
				//draw tutorial text
				super.drawIntertrial( canvas, activePage.getText(), activePage.getButtonText() );
				break;
			//break time
			case Experiment.ON_BREAK:
				super.drawText( canvas, activePage.getText(), 300 );
				super.drawButton( activePage.getButtonText(), 750 );
				break;
			//finishing the experiment
			case Experiment.FINISHED:
				super.drawText( canvas, activePage.getText(), 500 );
				break;
		}
		
		//draw buttons (if any)
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
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		
		int b;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//pick buttons
				b = pickButton( touchX, touchY );
				if ( b != -1 ) {
					pickedButtonIndex = b; //mark picked button
				}
				
				//draw path
				//when the user touches the View, move to that position to start drawing
				if ( parentController.getState() == Experiment.ON_TRIAL ) {
					drawPath.moveTo(touchX, touchY);
					
					//log coordinate
				    parentController.logCoordinate( "TOUCH_DOWN,"+touchX+";"+touchY );
				    
				    //mark start time
				    startTime = System.nanoTime();
				}
				
			    break;
			    
			case MotionEvent.ACTION_MOVE:
				//when they move their finger on the View, draw the path along with their touch
				if ( parentController.getState() == Experiment.ON_TRIAL ) {
					drawPath.lineTo(touchX, touchY);
					
					//log coordinate
				    parentController.logCoordinate( "TOUCH_MOVE,"+touchX+";"+touchY );
				}
			    break;
			    
			case MotionEvent.ACTION_UP:
			    //reset path in-motion
				//when they lift their finger up off the View, draw the Path and reset it for the next drawing operation
				if ( parentController.getState() == Experiment.ON_TRIAL ) {
					//log coordinate
				    parentController.logCoordinate( "TOUCH_UP,"+touchX+";"+touchY );
				    
				    //save the canvas into jpeg file
				    invalidate();
				    parentController.screenshot( this );
				    
					drawPath.reset();
					hideKeyboard();
					
					this.next();
				}

				b = pickButton( touchX, touchY );
				if ( b != -1 && pickedButtonIndex == b ) {
					//if the NEXT button is pressed
					if ( buttons.get(pickedButtonIndex).getText().equalsIgnoreCase("next") ) {
						//if the answer is provided during the post-trial,
						//then continue, else ignore 
						if ( parentController.getState() == Experiment.POST_TRIAL ) {
							if ( answer != null ) {
								//log the choice of post-questioner
								parentController.log( answer + "," + String.format("%.4f", time) );
								answer = null;
								time = 0;
								
								next();
							}
							
							//reset picked button index
							pickedButtonIndex = -1;
						}
						else {
							this.next();
							
							//reset picked button index
							pickedButtonIndex = -1;
						}
					}
					//if the "Yes", "No", or "Not sure" button is pressed
					else {
						answer = buttons.get(pickedButtonIndex).getText();
						time = (double)(System.nanoTime() - startTime)/1000000;
						startTime = 0;
					}
				}
				
			    break;
		}
		
		//call onDraw
		invalidate();
		
		return true;
	}
	
	
	private void next () {
		//ask controller to update the state
		parentController.next();
		
	}
	
	
	@Override
	public void finish () {
		activePage = new Page( "That's it for the experiment!; ;Thank you! :)", false, null );
		hideKeyboard();
	}
	
	
	@Override
	public void setExperimentParameter ( String word, String instruction, String trialID, int state ) {
		this.word = word;
		this.instruction = instruction;
		this.trialID = trialID;
		
		activePage = new Page();
		
		switch ( state ) {
			//it should never happen
			case Experiment.PRACTICE:
				//do nothing
				break;
				
			//starting the experiment
			case Experiment.INTER_TRIAL:
				//create a new page for inter trial
				activePage.initInterTrial( this.trialID, this.instruction );
				break;
	
			//starting a trial
			case Experiment.ON_TRIAL:
				trialCounter++;
				
				//create a new page for on-trial
				activePage.initTrial( this.trialID, this.word, this.instruction );
				break;
			
			//finishing a trial
			case Experiment.POST_TRIAL:
				//create a new page for post-trial
				activePage.initPostTrial( this.trialID, this.word, trialCounter );
				break;
			
			//starting break
			case Experiment.ON_BREAK:
				activePage.initBreak( trialCounter );
				break;
		}
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

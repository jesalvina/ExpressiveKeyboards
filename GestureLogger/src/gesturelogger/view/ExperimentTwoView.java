package gesturelogger.view;

import gesturelogger.model.Constant;
import gesturelogger.model.Experiment;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ExperimentTwoView extends DrawingView {
	/* VARIABLE DECLARATIONS */
	//the active word
	private String word;
	//the active instruction
	private String instruction;
	//the active trial ID
	private String trialID;
	//the time to finish a gesture
	private int trialCounter, repetitionCounter = 0, repetitions[] = new int[] {0,0,0};
	private Page activePage;

	static TextWatcher textWatcher;
	
	
	public ExperimentTwoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//set default value
		trialCounter = 0;
		trialID = "";
		word = "";
		instruction = "";
		
		//erase all output
		resetAllOutput();
		
		textWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
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
						
						if ( activePage.hasOutput() ) {
						    new Handler().postDelayed(new Runnable() { 
						         @Override
								public void run() { 
						        	 if ( activePage.getState() == Experiment.ON_TRIAL ) {
						        		 repetitionCounter++;
						        		 //log coordinate: (trialNo)(repNo),(timestamp),(coordX),(coordY)
						        		 processOutput( trialCounter + "," + repetitionCounter, activePage.getOutputType() );
						        	 }
						        	 else {
						        		 processOutput( null, activePage.getOutputType() );
						        	 }
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
		if ( activePage.hasKeyboard() && outputDisplay == null ) {
			outputDisplay = parentController.showKeyboard();
			outputDisplay.addTextChangedListener(textWatcher);
		}
		else if ( !activePage.hasKeyboard() && outputDisplay != null ) {
			parentController.hideKeyboard( this, outputDisplay );
			outputDisplay = null;
		}

		//on trial
		switch ( parentController.getState() ) {
			//during trial
			case Experiment.ON_TRIAL:
				drawTrial( canvas, activePage.getText() );

				//draw output
				drawOutput( canvas, Constant.COLORED_OUTPUT, 330, 28, 55 );
				
				//has drawn all the words in output
				if ( outputs.size() == activePage.getWord().split(" ").length ) {
					drawButton( activePage.getButtonText(), 590 );
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
				parentController.logCoordinate();
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
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();

		this.requestFocus();
		
		int b;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//pick buttons
				b = pickButton( touchX, touchY );
				if ( b != -1 ) {
					pickedButtonIndex = b; //mark picked button
				}
				
				//pick radio button
				if ( radioGroups != null ) {
					String tmp = radioGroups.pickRadioButton(touchX, touchY);
					if ( tmp != null ) {
						pickedRadiobuttonID[Character.getNumericValue(tmp.charAt(0))] = tmp;
					}
				}
				
			    break;
			    
			case MotionEvent.ACTION_MOVE:
			    break;
			    
			case MotionEvent.ACTION_UP:
			    //handle button-picking
			    b = pickButton( touchX, touchY );
				if ( b != -1 && pickedButtonIndex == b ) {
					//if the NEXT button is pressed
					if ( buttons.get(pickedButtonIndex).getText().equalsIgnoreCase("next") ) {
						//if the answer is provided during the post-trial,
						//then continue, else ignore 
						if ( parentController.getState() == Experiment.POST_TRIAL ) {
							//if there's answer during the post-trial OR it's not during post-trial
							if ( pickedRadiobuttonID[0] != "" && pickedRadiobuttonID[1] != "" && pickedRadiobuttonID[2] != "" ) {
								//calculate correct rate
								int correctRate = getCorrectRate( instruction );
								
								//log it
								for ( int i=0; i<Constant.NBREPETITIONS; i++ ) {
									//(trialNo),(totalRep),(meanAngularity),(meanSize),(meanSpeedRatio),
									//(red),(green),(blue),(correctRate),(satisfactionRate)
									parentController.log( trialCounter-(3-i) + "," + (repetitions[i]-word.split(" ").length) + "," + 
											allOutputs.get(i).getAngularity() + "," + allOutputs.get(i).getSizeRatio() + "," + allOutputs.get(i).getSpeedRatio() + "," + 
											allOutputs.get(i).red + "," + allOutputs.get(i).green + "," + allOutputs.get(i).blue + "," + 
											correctRate + "," + radioGroups.get(pickedRadiobuttonID[i]).getValue() );
								}
								
								//reset radioGroups
								pickedRadiobuttonID = new String[] {"","",""};
								
								//erase output text
								output = "";
								if ( outputDisplay != null ) {
									outputDisplay.setText("");
								}
								
								this.next();
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
					else if ( buttons.get(pickedButtonIndex).getText().equalsIgnoreCase("NEXT PHRASE") ) {
						Log.v("trialCounter",trialCounter+" "+repetitionCounter);
						if ( (trialCounter%Constant.NBREPETITIONS) != 0 ) {
							repetitions[(trialCounter-1)%3] = repetitionCounter;
							trialCounter++;
							repetitionCounter = 0;
							addPhraseToOutput();
							resetOutput();
							invalidate();
							outputDisplay.setText("");
						}
						else {
							repetitions[(trialCounter-1)%3] = repetitionCounter;
							addPhraseToOutput();
							invalidate();
							resetOutput();
							outputDisplay.setText("");

						    //save the canvas into jpeg file
						    parentController.screenshot( this );
						    
							this.next();
						}
						
						//reset picked button index
						pickedButtonIndex = -1;
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
	 * to calculate the correct score (0/1/2) in Experiment 2, Block 1
	 * @param instruction
	 * @return correct score (0/1/2)
	 */
	protected int getCorrectRate ( String instruction ) {
		int r = 0, g = 0, b = 0;
		if ( instruction.equals(Constant.INSTRUCTIONS[0]) ) {
			for ( int i=0; i < allOutputs.size(); i++ ) {
				OutputView row = allOutputs.get(i);
				Log.v("black "+i,
						r+ " "+g+" "+b+" - "+row.red+" "+row.green+" "+row.blue);
				
				//set threshold
				if ( i == 0 ) {
					r = row.red;
					g = row.green;
					b = row.blue;
				}
				else {
					if ( (Math.abs( r - row.red)) <= 30 && 
							(Math.abs( g - row.green)) <= 30 &&
							(Math.abs( b - row.blue)) <= 100  ) {
						//do nothing
					}
					else {
						return 0;
					}
				}
			}
			return 1;
		}
		//shades
		else if ( instruction.equals(Constant.INSTRUCTIONS[1]) ) {
			for ( int i=0; i < allOutputs.size(); i++ ) {
				OutputView row = allOutputs.get(i);
				Log.v("green "+i, r+ " "+g+" "+b+" - "+row.red+" "+row.green+" "+row.blue);
			
				//set threshold
				if ( i == 0 ) {
					r = row.red;
					g = row.green;
					b = row.blue;
				}
				else {
					if ( row.green > 60 && (row.green > row.red || row.green > row.blue) &&
							( ( Math.abs( r - row.red ) >= 20 ) || 
							  ( Math.abs( g - row.green ) >= 20 )|| 
							  ( Math.abs( b - row.blue ) >= 20 ) ) ) {
						//do nothing
					}
					else {
						return 0;
					}
				}
			}
			return 1;
		}
		//gradient
		else if ( instruction.equals(Constant.INSTRUCTIONS[2]) ) {
			for ( int i=0; i < allOutputs.size(); i++ ) {
				OutputView row = allOutputs.get(i);
				Log.v("blue "+i, r+ " "+g+" "+b+" - "+row.red+" "+row.green+" "+row.blue);
			
				//set threshold
				if ( i == 0 ) {
					r = row.red;
					g = row.green;
					b = row.blue;
				}
				else {
					if ( row.blue > 150 &&
							( ( Math.abs( r - row.red ) >= 20 ) || 
							  ( Math.abs( g - row.green ) >= 20 )|| 
							  ( Math.abs( b - row.blue ) >= 20 ) ) ) {
						//do nothing
					}
					else {
						return 0;
					}
				}
			}
			return 1;
		}
		//scribble
		else if ( instruction.equals(Constant.INSTRUCTIONS[3]) ) {
			for ( int i=0; i < allOutputs.size(); i++ ) {
				OutputView row = allOutputs.get(i);
				Log.v("scribble "+i, row.red+" "+row.green+" "+row.blue);
			
				if ( row.green >= 150 || row.blue >= 150 ) {
					//do nothing
				}
				else {
					return 0;
				}
			}
			return 1;
		}
		//speed
		else if ( instruction.equals(Constant.INSTRUCTIONS[4]) ) {
			double speed = 0;
			for ( int i=0; i < allOutputs.size(); i++ ) {
				OutputView row = allOutputs.get(i);
					Log.v("speed "+i, speed+" "+row.getSpeed());
					
				//set threshold
				if ( i == 0 ) {
					speed = row.getSpeed();
				}
				else {
					if ( Math.abs( speed - row.getSpeed() ) >= 100 ) {
						//do nothing
					}
					else {
						return 0;
					}
				}
			}
			return 1;
		}
		//go outside
		else if ( instruction.equals(Constant.INSTRUCTIONS[5]) ) {
			for ( int i=0; i < allOutputs.size(); i++ ) {
				OutputView row = allOutputs.get(i);
				Log.v("outside "+i, r+ " "+g+" "+b+" - "+row.red+" "+row.green+" "+row.blue);
			
				if ( row.red > 50 ) {
					//do nothing
				}
				else {
					return 0;
				}
				
			}
			return 1;
		}
		else {
			return 1;
		}
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
		
		if ( activePage == null ) activePage = new Page();
		
		switch ( state ) {
			//it should never happen
			case Experiment.PRACTICE:
				//do nothing
				break;
				
			//starting the experiment
			case Experiment.INTER_TRIAL:
				//create a new page for inter trial
				activePage.initInterTrial( this.trialID, this.instruction );
				
				//erase all output for every new instruction
				repetitionCounter = 0;
				resetAllOutput();
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

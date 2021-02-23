package gesturelogger.view;

import gesturelogger.model.CharacterPath;
import gesturelogger.model.Constant;
import gesturelogger.model.Experiment;
import gesturelogger.model.ExperimentThree;
import gesturelogger.model.ExperimentTwo;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ScrollView;

public class ExperimentThreeView extends DrawingView {
	/* VARIABLE DECLARATIONS */
	//the active word
	private String sentence;
	//the active output type
	private int outputType;
	//the active trial ID
	private String trialID;

	//trial management
	private int trialCounter, quizCounter = 0, wordCounter = 0, clearCounter = 0;
	private int top = 370; //position of output during quiz time
	private Page activePage;

	private static TextWatcher textWatcher;
	private static OnClickListener buttonListener;
	
	//to handle scrollview
	private int desiredWidth, desiredHeight;
	
	
	public ExperimentThreeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//set default value
		trialCounter = 0;
		trialID = "";
		sentence = "";
		
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
					if ( output.length() > 1 && output.charAt(output.length()-2) != ' ' ) output += " ";
				}
				//typing & gesturing
				else {
					if ( before < count ) {
						output = s+"";
						
						if ( activePage.hasOutput() ) {
						    new Handler().postDelayed(new Runnable() { 
						         @Override
								public void run() { 
						        	 if ( activePage.getState() == Experiment.ON_TRIAL ) {
						        		 wordCounter++;
						        		 
						        		 //log coordinate: (trialNo),(wordNo),(timestamp),(coordX),(coordY)
						        		 processOutput( trialCounter + "," + wordCounter, activePage.getOutputType() );
						        	 }
						        	 else if ( activePage.getState() == Experiment.ON_QUIZ ) {
							       		 //update position of output
							       		 if ( quizCounter > 0 ) top += ( ((int)(Constant.FONT_HEIGHT*fontScale)) + 10 );
						        		 
							       		 //update quiz counter, stop after 3
						        		 quizCounter++;
							       		 
						        		 //log coordinate: QUIZ,(quizNo),(timestamp),(coordX),(coordY)
						        		 processOutput( "QUIZ," + quizCounter, activePage.getOutputType() );
						        		 
						        		 OutputView w;
						        		 for ( int i=0; i<outputs.size(); i++ ) {
						 	        		w = outputs.get(i);
						 	        		
						 	        		//trialNo,wordNo,word,r,inflation,g,angularity,b,speedRatio,avgSpeed
						 	        		parentController.log( quizCounter + "," + i + "," + w.getWord() + "," + 
						 	        								w.red + "," + w.getSizeRatio() + "," + 
						 	        								w.green + "," + w.getAngularity() + "," + 
						 	        								w.blue + "," + w.getSizeRatio() + "," +
						 	        								w.getSpeed() + "," +
						 	        								(w.getWord().equals(activePage.getWord())) + "," +
						 	        								"0" + "," + //total wrong word
						 	        								getCorrectRate( w, sentence )[0] + "," +
						 	        								getCorrectRate( w, sentence )[1] + "," +
						 	        								getCorrectRate( w, sentence )[2] + "," 
						 	        							);
						 	        	}
						        	 }
						        	 else {
						        		 processOutput( null, activePage.getOutputType());
						        	 }
									 invalidate();
						         } 
						    }, 500);
						}
					}
					//if there is a deletion, delete the whole word
					else if ( s.length() < output.length() ) {
						if ( activePage.getState() == Experiment.ON_TRIAL ) {
							eraseOutput(true);
						}
						else {
							eraseOutput();
						}
					}
				
				}
				outputDisplay.setSelection(outputDisplay.getText().length());
				invalidate();
				
				//scroll
				if ( !outputs.isEmpty() ) {
					ScrollView sv = (ScrollView)getParent();
					sv.smoothScrollTo(0, (int) outputs.get(outputs.size()-1).getY()-400);
				}
			}
	    };
	    
	    
	    //initialize button to inspect clicking
	    buttonListener = new View.OnClickListener() {
	        public void onClick(View view) {
	        	if ( outputs.size() == activePage.getWord().split(" ").length ) {
		        	//hide the keyboard and take a screenshot
		        	parentController.hideKeyboard( ExperimentThreeView.this, outputDisplay );
					outputDisplay = null;
		        	parentController.screenshot( ExperimentThreeView.this );
		        	
		        	//LOG
		        	OutputView w;
		        	for ( int i=0; i<outputs.size(); i++ ) {
		        		w = outputs.get(i);
		        		
		        		//trialNo,wordNo,word,TimeCompletionTime,r,inflation,g,angularity,b,speedRatio,avgSpeed
		        		parentController.log( trialCounter + "," + i + "," + w.getWord() + "," + 
		        								totalTime[i] + "," +
		        								w.red + "," + w.getSizeRatio() + "," + 
		        								w.green + "," + w.getAngularity() + "," + 
		        								w.blue + "," + w.getSpeedRatio() + "," +
		        								w.getSpeed() + "," +
		        								(w.getWord().equals((activePage.getWord().split(" ")[i]).toLowerCase().replaceAll("[^a-z ]", ""))) + "," +
		        								totalWrongWords[i] + "," +
		        								totalWrongOutputProperties[i]
		        							);
		        	}
		        	
		        	//reset output and the text field
		        	output = "";
					if ( outputDisplay != null ) {
						outputDisplay.setText("");
					}
		            next();
		            
		            //prepare falsity checking for the next page
		            if( activePage.getWord() != null ) {
		            	int n = activePage.getWord().split(" ").length;
		            	totalWrongOutputProperties = new int[30];
		            	totalWrongWords = new int[30];
		            	totalTime = new double[30];
		            }
					
		            invalidate();
	        	}
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
			//hide textfield
			parentController.hideKeyboard( this, outputDisplay );
			outputDisplay = null;
			
			//hide button
			parentController.hideKeyboard( this, sendButton );
			sendButton = null;
		}

		//on trial
		switch ( parentController.getState() ) {
			//during practice
			case Experiment.PRACTICE:
				//set the top position
				int textY = 50;
				if ( !activePage.hasKeyboard() ) {
					textY = 200;
				}
				
				//draw tutorial text
				super.drawText( canvas, activePage.getText(), textY );
				
				//draw output field
				if ( activePage.hasOutput() ) {
					drawOutput( canvas, activePage.getOutputType(), 450, 25, 30 );
				}
				
				//draw buttons
				//if there's only one button, draw in the middle
				if ( activePage.getButtonText() != null ) {
					drawButton( activePage.getButtonText(), 1000 );
				}
				//if there are more than one, draw at a specific location
				else if ( activePage.getButtonTexts() != null ) {
					String tmp[] = activePage.getButtonTexts();
					drawButton( tmp[0], 550, 320 );
					drawButton( tmp[1], 810, 320 );
				}
				
				break;
			//during trial
			case Experiment.ON_TRIAL:
				drawTrial( canvas, activePage.getText() );

				//draw output
				drawOutput( canvas, activePage.getOutputType(), 420, 25, 30 );
				
				//draw button
				drawButton( activePage.getButtonText(), this.getWidth() - 240, 250 );
				
				//has drawn all the words in output
				//if ( outputs.size() == activePage.getWord().split(" ").length && sendButton == null ) {
				if ( sendButton == null ) {
					sendButton = parentController.showButton("SEND");
					sendButton.setOnClickListener(buttonListener);
				}
				
				break;
			//after trial: the post-questionnaire
			case Experiment.POST_TRIAL:
				super.drawPosttrial ( canvas, activePage.getText(), activePage.getButtonTexts() );
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
				
			//starting quiz
			case Experiment.INTER_QUIZ:
				//draw tutorial text
				super.drawIntertrial( canvas, activePage.getText(), activePage.getButtonText() );
				break;
				
			//on quiz
			case Experiment.ON_QUIZ:
				super.drawQuiz( canvas, activePage.getText() );
				
				//draw output example
				//draw the word
				OutputView w;
				//BOLD or RED
				if ( sentence.equals( Constant.QUIZ_INSTRUCTION[0] ) ) {
					w = new OutputView( activePage.getWord(), Constant.DYNAMIC_OUTPUT, 255, 0, 0, 5, 1, this.getWidth() );
				}
				//ITALIC or BLUE
				else if ( sentence.equals( Constant.QUIZ_INSTRUCTION[1] ) ) {
					w = new OutputView( activePage.getWord(), Constant.DYNAMIC_OUTPUT, 0, 0, 255, 1, 5, this.getWidth() );
				}
				//GREEN
				else {
					w = new OutputView( activePage.getWord(), Constant.DYNAMIC_OUTPUT, 0, 255, 0, 1, 1, this.getWidth() );
				}
				w.generateDynamicOutput( (this.getWidth()/2)-200, 150, 0.3, this.getWidth() );
				//for each character
				CharacterPath cp;
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
				

				//draw output
				super.drawOutput( canvas, activePage.getOutputType(), top, 25, 30 );
				
				if ( !outputs.isEmpty() && outputs.get(0) != null && this.quizCounter < 3 ) {
					addPhraseToOutput( outputs.get(0) );
					resetOutput();
					invalidate();
					outputDisplay.setText("");
				}
       		 
       		 	if ( quizCounter >= 3 ) {
	       			 //next();
       		 		parentController.hideKeyboard( this, outputDisplay );
       		 		
       		 		drawButton( activePage.getButtonText(), this.getWidth()-300, this.getHeight()-500 );
	       		 }
				
				break;
				
			//ending quiz
			case Experiment.POST_QUIZ:
				super.drawPosttrial(canvas, activePage.getText(), activePage.getButtonTexts());
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

		this.requestFocus();
		
		int b;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//pick buttons
			b = pickButton( touchX, touchY );
			if ( b != -1 ) {
				pickedButtonIndex = b; //mark picked button
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
					if ( activePage.getState() == Experiment.ON_QUIZ ) {
			        	//hide the keyboard and take a screenshot
			        	parentController.hideKeyboard( ExperimentThreeView.this );
			        	parentController.screenshot( ExperimentThreeView.this );
					}
					
					this.next();
					
					//erase output
					super.resetAllOutput();
					quizCounter=0;
					top = 370;
				}
				//during the writing
				else if ( buttons.get(pickedButtonIndex).getText().equalsIgnoreCase("clear") ) {
					if ( outputs.isEmpty() ) {
						clearCounter++;
						
						if ( clearCounter == 3 ) {
							clearCounter = 0;
							this.next();
						}
					}
					
					super.resetAllOutput();
					outputDisplay.setText("");
				}
			}
			
			//reset picked button index
			pickedButtonIndex = -1;
			
		    break;
		}
		
		//call onDraw
		invalidate();
		
		return true;
	}
	
	
	private double[] getCorrectRate ( OutputView w, String instruction ) {
		double score[] = {0,0,0},
			   strokeWidth = (double)(w.getStrokeWidth()-10) / Constant.STROKE_WIDTH,
			   greenColor = w.green/255.0;
		//BOLD or RED
		if ( instruction.equals( Constant.QUIZ_INSTRUCTION[0] ) ) {
			score[0] = strokeWidth; 				//HIGH RED
			score[1] = ( 1 - greenColor );			//low green
			score[2] = w.getNormalizedSpeedRatio();	//low blue
		}
		//ITALIC or BLUE
		else if ( instruction.equals( Constant.QUIZ_INSTRUCTION[1] ) ) {
			score[0] = ( 1 - strokeWidth ); 				//low red
			score[1] = ( 1 - greenColor );					//low green
			score[2] = ( 1 - w.getNormalizedSpeedRatio() );	//HIGH BLUE
		}
		//GREEN
		else if ( instruction.equals( Constant.QUIZ_INSTRUCTION[2] ) ) {
			score[0] = ( 1 - strokeWidth ); 		//low red
			score[1] = greenColor;					//HIGH GREEN
			score[2] = w.getNormalizedSpeedRatio();	//low blue
		}
		
		return score;
	}
	
	
	/**
	 * ask controller to update the state
	 */
	private void next () {
		parentController.next();		
	}
	
	
	@Override
	public void finish () {
		activePage = new Page( "That's it for the experiment!; ;Thank you! :)", false, null );
		hideKeyboard();
	}
	
	
	public void setExperimentParameter ( String sentence, String instruction, String trialID, int state ) {
		this.sentence = sentence;
		this.trialID = trialID;
		//System.out.println(this.sentence+" "+this.instruction+" "+this.trialID+" "+state);
		
		activePage = new Page();
		
		switch ( state ) {
			//practice with the dynamic output
			case Experiment.PRACTICE:
				this.quizCounter = 0;
				super.resetAllOutput();
				
				
				switch ( this.outputType ) {
				case 0:
					activePage = new Page ( "Now, you can control;"
											+ "the font and color of your text;"
											+ "by how you write each word.",
											false,
											new String[] { "NEXT" }
									);
					break;
				case 1:
					activePage = new Page ( "If you write bigger,;"
											+ "the text becomes bold and red.;"
											+ " ;"
											+ "If you write curvier,;"
											+ "the text becomes messy and green.;"
											+ " ;"
											+ "If you write faster (or slower),;"
											+ "the text becomes italic and blue.;"
											+ " ;",
											false,
											new String[] { "NEXT" }
									);
					break;
				case 2:
					activePage = new Page ( "Now let's practice.;"
											+ " ;"
											+ "Can you control colors independently?",
											true,
											"",
											new String[] { "CLEAR", "NEXT" },
											Constant.DYNAMIC_OUTPUT
									);
					break;
				case 3:
					activePage = new Page ( "Great!;"
											+ " ;"
											+ "Let's continue with;"
											+ "the dynamic output.;",
											false,
											new String[] { "NEXT" }
									);
					break;
				}
				
				break;
				
			//starting the experiment
			case Experiment.INTER_TRIAL:
				//create a new page for inter trial
				activePage.initInterTrial( this.trialID, this.sentence );
				
				//erase all output for every new instruction
				quizCounter = 0;
				wordCounter = 0;
				resetAllOutput();
	            
	            //prepare falsity checking for the next page
            	int n = this.sentence.split(" ").length;
            	totalWrongOutputProperties = new int[30];
            	totalWrongWords = new int[30];
            	totalTime = new double[30];
	            
				break;
	
			//starting a trial
			case Experiment.ON_TRIAL:
				trialCounter++;
				//create a new page for on-trial
				activePage.initTrial( this.trialID, this.outputType, this.sentence );
				break;
			
			//finishing a trial
			case Experiment.POST_TRIAL:
				//create a new page for post-trial
				activePage.initPostTrial( this.trialID, this.sentence, trialCounter );
				break;
			
			//starting quiz
			case Experiment.ON_BREAK:
				activePage.initBreak( trialCounter );
				break;
				
			//starting quiz
			case Experiment.INTER_QUIZ:
				activePage.initInterQuiz(this.sentence);
				break;
				
			//on quiz
			case Experiment.ON_QUIZ:
				activePage.initQuiz(this.sentence);
				break;
				
			//ending quiz
			case Experiment.POST_QUIZ:
				activePage.initPostQuiz();
				break;
		}
	}
	
	
	/**
	 * instruction is always NULL because there is no instruction but to write the output with different type of output
	 */
	public void setExperimentParameter ( String sentence, String instruction, String trialID, int state, int outputType ) {
		setExperimentParameter ( sentence, instruction, trialID, state );
		this.outputType = outputType;
	}


	@Override
	public String getWordAt(int index) {
		String tmp[] = activePage.getWord().split(" ");
		return ( tmp.length > index ? tmp[index] : null );
	}
		

}

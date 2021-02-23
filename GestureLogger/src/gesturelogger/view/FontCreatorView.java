package gesturelogger.view;

import gesturelogger.model.CharacterPath;
import gesturelogger.model.Constant;

import java.util.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * class TutorialView
 * an interface to let users draw a dynamic font
 * @author jalvina
 *
 */
public class FontCreatorView extends DrawingView {
	/* VARIABLE DECLARATIONS */
	ArrayList <Page> pages;
	private int pageIndex = -1;
	static TextWatcher textWatcher;
	
	private boolean isAfterGesture;
	private boolean isDrawingCharacter;
	private CharacterPath charPath;
	private Point ctrPointToChange;
	private Stack<Point> pointStackBuffer;
	private char lastChar;
	private int topY = 700;
	private int desiredWidth, desiredHeight;
	private int totalControlPoint; //to make sure that the variation of a font has the exact same number of control point 

	public FontCreatorView(Context context, AttributeSet attrs) {
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
					//do nothing
				}
				else {
					if ( before < count ) {
						output = s+"";
						
						if ( pages.get(pageIndex).hasOutput() ) {
						    new Handler().postDelayed(new Runnable() { 
						         @Override
								public void run() { 
						        	 processOutput( null, Constant.DYNAMIC_OUTPUT );
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
					sv.smoothScrollTo(0, (int) outputs.get(outputs.size()-1).getY()-150);
				}
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
		
		//check for existing character paths
		lastChar = 'z';
		for ( int i='a'; i <= 'z'; i++ ) {
			if ( Constant.FONT.get((char)i) == null ) {
				lastChar = (char) (i-1); //the last index of existing character path
				break;
			}
		}
		
		//if all characters have been defined and it has baseline,
		//ask the participant if she wants to edit (the variation) or skip
		if ( lastChar == 'z' && parentController.hasBaselineFont() ) {
			pages.add( new Page ("The font you chose has been defined.;"
								+ " ;"
								+ "Press \"EDIT\" to edit from the beginning.;"
								+ " ;"
								+ "Press \"SKIP\" to continue.;",
							false,
							new String[] {"EDIT","SKIP"}
					) );
		}
		//if all characters have been defined and it doesn't baseline,
		//ask the participant if she wants to vary, edit (the baseline), or skip
		else if ( lastChar == 'z' && !parentController.hasBaselineFont() ) {
			pages.add( new Page ("The font you chose has been defined.;"
								+ " ;"
								+ "Press \"CREATE\" to create a variation.;"
								+ " ;"
								+ "Press \"EDIT\" to edit from the beginning.;"
								+ " ;"
								+ "Press \"SKIP\" to continue.;",
							false,
							new String[] {"CREATE","EDIT","SKIP"}
					) );
		}
		//if any character has been defined, ask the participant if she wants to edit from the beginning or skip it
		else if ( lastChar != 96 ) {
			pages.add( new Page ("The font you chose has been defined;"
									+ "up to character '" + (char)lastChar + "'.;"
									+ " ;"
									+ "Press \"EDIT\" to edit from the beginning.;"
									+ " ;"
									+ "Press \"SKIP\" to continue;"
									+ "where it's left off.;",
								false,
								new String[] {"EDIT","SKIP"}
					) );
		}
		//if not, start from the beginning
		else {
			pages.add( new Page ( "You will define a font face;"
					+ "from 'a' to 'z'.;"
					+ " ;"
					+ "Let's start.",
				false,
				new String[] {"NEXT"}
					) );
			
			//from 'a' to 'z'
			this.createCharacterDrawingPage( 'a' );
		}
	}
	
	
	private void createCharacterDrawingPage ( int start ) {
		for ( int i=start; i <= 122; i++ ) {
			pages.add( new Page ("Draw '" + (char)i + "' by connecting dots.;"
					+ "Tap to add a dot,;"
					+ "drag to move a dot.",
					(char)i,
				new String[] {"DETACH", "UNDOT", "REDOT", "DONE"}
					) );
		}
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
		}
		
		//draw the whole page
		//enable character drawing
		if ( pages.get(pageIndex).getCharacter() != '\u0000' ) {
			isDrawingCharacter = true;
			
			//draw the guidelines
			canvas.drawLine(300, topY, 750, topY, rectPaint);
			canvas.drawLine(300, topY+(Constant.FONT_HEIGHT/3), 750, topY+(Constant.FONT_HEIGHT/3), linePaint);
			canvas.drawLine(300, topY+(Constant.FONT_HEIGHT/3*2), 750, topY+(Constant.FONT_HEIGHT/3*2), rectPaint);
			canvas.drawLine(300, topY+(Constant.FONT_HEIGHT), 750, topY+(Constant.FONT_HEIGHT), linePaint);
			
			//draw control points
			ArrayList<Point> c;
			int totalCP = 0;
			for ( int i=0; i < charPath.getPoints().size(); i++ ) {
				c = charPath.getPoints().get(i);
				for ( int j=0; j<c.size(); j++ ) {
					canvas.drawCircle(c.get(j).x, c.get(j).y, Constant.CONTROLPOINT_RAD, ctrpointPaint);
					
					totalCP++;
				}
			}
			if ( totalControlPoint != -1 && totalControlPoint != totalCP ) {
				canvas.drawText( ( (totalControlPoint - totalCP) > 0 ? "Add " : "Delete " ) + Math.abs(totalControlPoint - totalCP) + " point(s)" ,
									300, topY-40, super.wordPaint);
			}
			
			//draw spline
			charPath.generatePath();
			for ( int i=0; i < charPath.getPoints().size(); i++ ) {
				if ( charPath.getPath(i) != null ) {
					if ( ctrPointToChange != null ) {
						pathPaint.setColor(feedbackColor[0]);
					}
					else {
						pathPaint.setColor(pathColor);
					}
					
					canvas.drawPath( charPath.getPath( i ), pathPaint);
					//canvas.drawPath(outputPath.getPath( parentController.getSpline(), 100,5 ), pathPaint);
					//canvas.drawPath(outputPath2.getPath( parentController.getSpline(), 10,50 ), pathPaint);
					
					isAfterGesture = true;
				}
			}
		}
		else {
			isDrawingCharacter = false;
		}
		
		//draw instruction
		super.drawText( canvas, activePage.getText(), ( isDrawingCharacter || activePage.hasKeyboard() ? 150 : 300 ) );
		
		//draw output field
		if ( pages.get(pageIndex).hasOutput() ) {
			drawOutput( canvas, Constant.DYNAMIC_OUTPUT );
		}
		
		//draw buttons
		if ( activePage.getButtonText() != null ) {
			drawButton( activePage.getButtonText(), 900 );
		}
		else if ( activePage.getButtonTexts() != null ) {
			String tmp[] = activePage.getButtonTexts();
			
			//for instruction page
			if ( activePage.getCharacter() == '\u0000' ) {
				for ( int i=(tmp.length-1); i>-1; i-- ) {
					drawButton( tmp[i], (830-(280*(tmp.length-1-i))), (  activePage.hasKeyboard() ? 280 : 900 ) );
				}
			}
			//for character drawing page
			else if ( isAfterGesture ) {
				int gap;
				for ( int i=(tmp.length-1); i>-1; i-- ) {
					gap = ( i == 0 ? 260: 245 );
					drawButton( tmp[i], (830-(gap*(tmp.length-1-i))), 500 );
				}
			}
		}
			
		//draw button on canvas (if any)
		for ( int i=0; i<buttons.size(); i++ ) {
			WOZButtonProperty b = buttons.get(i);
			
			if ( i == pickedButtonIndex ) {
				b.pick();
			}
			
			//draw the rectangle
			//canvas.drawRoundRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, 10, 10, b.getButtonPaint() );
			//canvas.drawRoundRect( b.LEFT, b.TOP, b.RIGHT, b.BOTTOM, 10, 10, b.getButtonRPaint() );
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
					if ( buttonText.equalsIgnoreCase("next") || buttonText.equalsIgnoreCase("start") ) {
						this.nextPage();
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					//during the character-drawing loading
					else if ( buttonText.equalsIgnoreCase("edit") ) {
						//create from the beginning
						createCharacterDrawingPage( 'a' );
						
						this.nextPage();
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					//during the character-drawing loading
					else if ( buttonText.equalsIgnoreCase("create") ) {
				        //duplicate the current definition into the baseline
						parentController.duplicateCharacterAsBaseline();
						
						//create from the beginning
						createCharacterDrawingPage( 'a' );
						
						this.nextPage();
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					//during the character-drawing loading
					else if ( buttonText.equalsIgnoreCase("skip") ) {
						//log the previous definition
						parentController.logCharacter(lastChar);
						
						//create from the beginning
						createCharacterDrawingPage( lastChar+1 );
						
						this.nextPage();
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					//during the character-drawing pages
					else if ( buttonText.equalsIgnoreCase("done") ) {
						if ( totalControlPoint == -1 || getTotalControlPoint( charPath ) == totalControlPoint ) {
							Constant.FONT.put( charPath.getCharacter(), charPath );
							parentController.logCharacter(charPath.toString(), false);
							this.nextPage();
						}
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					else if ( buttonText.equalsIgnoreCase("undot") ) {
						Point p = charPath.undot();
						if ( p != null ) {
							pointStackBuffer.push( p );
						}
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					else if ( buttonText.equalsIgnoreCase("redot") ) {
						if ( !pointStackBuffer.empty() ) {
							Point p = pointStackBuffer.pop();
							charPath.draw( p.x, p.y );
						}
						
						//reset picked button index
						pickedButtonIndex = -1;
					}
					else if ( buttonText.equalsIgnoreCase("detach") ) {
						charPath.detach();
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
				        //parentController.startExperiment();
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
			
			//create a character path object for the current page
			if ( pages.get(pageIndex).getCharacter() != '\u0000' ) {
				charPath = Constant.FONT.get( pages.get(pageIndex).getCharacter() );
				
				//note total control point to make sure that the variation has the same number of control points
				totalControlPoint = -1;
				if ( Constant.FONT_BASELINE.get( pages.get(pageIndex).getCharacter() ) != null ) {
					totalControlPoint = this.getTotalControlPoint( Constant.FONT_BASELINE.get( pages.get(pageIndex).getCharacter() ) );
				}
				
				if ( charPath == null ) {
					charPath = Constant.FONT_BASELINE.get( pages.get(pageIndex).getCharacter() );
					if ( charPath == null ) {
						charPath = new CharacterPath( pages.get(pageIndex).getCharacter(), topY );
					}
				}
			}
		}
		else {
			pages.add( new Page( "That's it !;"
							   + " ;"
							   + "You can start writing now!", 
					false, 
					new String[] {"START"} 
						) );
			
			pages.add( new Page ("Try gesture-typing.;",
					true,
					"",
					new String[] {"CLEAR","NEXT"},
					Constant.DYNAMIC_OUTPUT
						) );
			
			pages.add( new Page( "That's it !; ;Thank you!", 
					false, 
					null 
						) );
			
			this.nextPage();
		}
		
	}


	private int getTotalControlPoint ( CharacterPath charOri ) {
		int cp = 0;
		for ( int i=0; i<charOri.getPoints().size(); i++ ) {
			cp += charOri.getPoints(i).size();
		}
		System.out.println(charOri.getCharacter() + " " + cp);
		
		return cp;
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

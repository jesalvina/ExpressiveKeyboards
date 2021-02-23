package gesturelogger.model;

import java.util.Random;

import android.util.Log;

public class ExperimentThree {
	//Trial order used in the current session
	public int [] ActiveTextOrder;
	private int pid;
	private String fou;
	
	//index of instruction, which also represents the block number
	private int blockIndex = -1;
	//index of phrase index in each block (also represents the trial number)
	private int sentenceIndex = -1;
	//index of practice index
	private int practiceIndex = 0;
	//experiment state
	private int STATE;
	
	//total block before quiz: quiz is between block 2 and 3
	public static int blockBeforeQuiz = 2;
	
	
	//natural v.s. prescribed conversation in BLOCK: Latin-Square in Block 1&2
	public static int[][] INSTRUCTION_ORDER =
		    {
			  //B0-1-2 Q0-1-2 B3
				{1,0,1, 1,2,0, 1},
				{1,1,0, 2,0,1, 1},
				{1,0,1, 0,1,2, 1},
				{1,1,0, 1,2,0, 1},
				{1,0,1, 2,0,1, 1},
				{1,1,0, 0,1,2, 1},
				{1,0,1, 1,2,0, 1},
				{1,1,0, 2,0,1, 1},
				{1,0,1, 0,1,2, 1},
				{1,1,0, 1,2,0, 1},
				{1,0,1, 2,0,1, 1},
				{1,1,0, 0,1,2, 1}
		    };
	
	
	/**
	 * default constructor
	 * Set active trial order based on the participant ID
	 * @param pid
	 */
	public ExperimentThree ( int pid, String fou ) {
		this.pid = pid;
		this.fou = fou;
		
		ActiveTextOrder = ExperimentThree.INSTRUCTION_ORDER[(pid-1)%12];
		
		this.STATE = Experiment.PRACTICE;
	}
	
	
	public int getCurrentState () {
		return STATE;
	}
	
	
	public int getCurrentOutputType () {
		if ( blockIndex == 0 || blockIndex == ActiveTextOrder.length-1 ) {
			return Constant.STATIC_OUTPUT;
		}
		else {
			return Constant.DYNAMIC_OUTPUT;
		}
	}
	
	
	/**
	 * getWordID
	 * Called in the beginning and after a participant finishes typing a word with all the instructions
	 * @param order of the active wordset
	 * @param order of the active word
	 * @return the active word
	 */
	public String getSentence () {
		//on quiz
		if ( blockIndex >= 3 && blockIndex <=5 ) {
			return Constant.QUIZ_INSTRUCTION[ ActiveTextOrder[blockIndex] ];
		}
		else {
			return Constant.SENTENCES[ ActiveTextOrder[blockIndex] ][(pid-1)%12][sentenceIndex];
		}
	}
	
	
	public int getPracticeIndex() {
		return practiceIndex;
	}
	
	
	public int next () {
		int nextState = -1;
		
		switch ( this.STATE ) {
			case Experiment.PRACTICE:
				if ( blockIndex == -1 ) {
					nextState = Experiment.INTER_TRIAL;
					
					//set the new phrase
					sentenceIndex++;
					
					//set the new instruction
					blockIndex++;
				}
				else if ( practiceIndex > 3 ){
					nextState = Experiment.INTER_TRIAL;
				}
				else {
					practiceIndex++;
					nextState = Experiment.PRACTICE;
				}
				break;
			case Experiment.INTER_TRIAL:
				nextState = Experiment.ON_TRIAL;
				break;
			case Experiment.ON_TRIAL:
				if ( sentenceIndex < 4 ) {
					nextState = Experiment.ON_TRIAL;
					
					//set the new sentence
					sentenceIndex++;
				}
				else {
					nextState = Experiment.POST_TRIAL;
					
					//reset sentence index
					sentenceIndex = 0;
				}
				break;
			case Experiment.POST_TRIAL:
				//after each block
				//start practicing after block 0
				if ( blockIndex == 0 ) {
					nextState = Experiment.PRACTICE;
					practiceIndex++;
				}
				//start quiz after block 1 and 2
				else if ( blockIndex == blockBeforeQuiz ) {
					nextState = Experiment.ON_BREAK;
				}
				//finish all blocks
				else if ( blockIndex == (ActiveTextOrder.length-1) ) {
					nextState = Experiment.FINISHED;
				}
				else {
					//by default, continue to the next trial
					nextState = Experiment.INTER_TRIAL;
				}
				
				//set the new block
				blockIndex++;
				break;
				
			case Experiment.ON_BREAK:
				nextState = Experiment.INTER_QUIZ;
				break;
			case Experiment.INTER_QUIZ:
				nextState = Experiment.ON_QUIZ;
				break;
			case Experiment.ON_QUIZ:
				nextState = Experiment.POST_QUIZ;
				break;
			case Experiment.POST_QUIZ:
				//set the new block
				blockIndex++;
				
				if ( blockIndex != (ActiveTextOrder.length-1) ) {
					nextState = Experiment.INTER_QUIZ;
				}
				else {
					nextState = Experiment.INTER_TRIAL;
				}
				break;
			case Experiment.FINISHED:
				break;
		}
		
		this.STATE = nextState;
		
		return this.STATE;
	}
	
	
	public String getTrialDetail () {
		if ( blockIndex >= 3 && blockIndex <=5 ) {
			return ( this.getSentence() ) + "," +
					( this.getCurrentOutputType() == Constant.DYNAMIC_OUTPUT ? "DYNAMIC" : "STATIC" );
		}
		else {
			return ( ActiveTextOrder[blockIndex] == 0 ? "NATURAL" : "PRESCRIBED" ) + "," +
					( this.getCurrentOutputType() == Constant.DYNAMIC_OUTPUT ? "DYNAMIC" : "STATIC" );
		}
	}
	
	
	public String getTrialID () {
		if ( blockIndex >= 3 && blockIndex <=5 ) {
			return "P" + pid + "_" + "QUIZ-" + this.ActiveTextOrder[blockIndex];
		}
		else {
			return "P" + pid + "_" + blockIndex + "-" +
					( ActiveTextOrder[blockIndex] == 0 ? "NAT" : "PRE" ) + "-" +
					( this.getCurrentOutputType() == Constant.DYNAMIC_OUTPUT ? "DY" : "ST" ) + "-" +
					sentenceIndex;
		}
	}
}

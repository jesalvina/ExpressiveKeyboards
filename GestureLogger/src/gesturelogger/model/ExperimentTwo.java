package gesturelogger.model;

import java.util.Random;

import android.util.Log;

public class ExperimentTwo {
	//Trial order used in the current session
	public int [] ActiveInstructionOrder;
	private int pid;
	private String fou;
	
	//index in the phrase list
	private Random phraseIndexRandomGen;
	private int phraseIndex = -1;
	//index in the instruction list
	private int instructionIndex = -1;
	//index of word-list order in each block of wordset (also represents the block number)
	//experiment state
	private int STATE;
	//counter for break time
	
	//total words per sub-session before break
	public static int wordsBeforeBreak = 6;
	
	public static final int ALLSET = 0,
							SUBSET = 1;
	
	
	//word-list: Latin-Square, size 12x10
	public static int[][] INSTRUCTION_ORDER =
		    {
				{0,1,5,2,4,3,6,7,9,8},
				{1,2,0,3,5,4,7,8,6,9},
				{2,3,1,4,0,5,8,9,7,6},
				{3,4,2,5,1,0,9,6,8,7},
				{4,5,3,0,2,1,6,7,9,8},
				{5,0,4,1,3,2,7,8,6,9},
				{0,1,5,2,4,3,8,9,7,6},
				{1,2,0,3,5,4,9,6,8,7},
				{2,3,1,4,0,5,6,7,9,8},
				{3,4,2,5,1,0,7,8,6,9},
				{4,5,3,0,2,1,8,9,7,6},
				{5,0,4,1,3,2,9,6,8,7}
		    };
	
	
	/**
	 * default constructor
	 * Set active trial order based on the participant ID
	 * @param pid
	 */
	public ExperimentTwo ( int pid, String fou ) {
		this.pid = pid;
		this.fou = fou;
		
		ActiveInstructionOrder = ExperimentTwo.INSTRUCTION_ORDER[pid%12];
		
		phraseIndexRandomGen = new Random();
		
		this.STATE = Experiment.PRACTICE;
	}
	
	
	public int getCurrentState () {
		return STATE;
	}
	
	
	/**
	 * getWordID
	 * Called in the beginning and after a participant finishes typing a word with all the instructions
	 * @param order of the active wordset
	 * @param order of the active word
	 * @return the active word
	 */
	public String getPhrase () {
		return Constant.PHRASES[ phraseIndex ];
	}
	
	
	public String getInstruction () {
		return Constant.INSTRUCTIONS[ ActiveInstructionOrder[instructionIndex] ];
	}
	
	
	
	public String getTrialDetail () {
		return Constant.TRIAL_DETAIL[ ActiveInstructionOrder[instructionIndex] ];
	}
	
	
	public String getTrialID () {
		return "P"+pid + "-" + 
				fou.charAt(0) + "-" + 
				Constant.TRIAL_ID[ ActiveInstructionOrder[instructionIndex] ];
	}
	
	
	private void resetBreak () {
		wordsBeforeBreak = 5;
	}
	
	
	public int next () {
		int nextState = -1;
		
		switch ( this.STATE ) {
			case Experiment.PRACTICE:
				nextState = Experiment.INTER_TRIAL;
				
				//set the new phrase
				phraseIndex = phraseIndexRandomGen.nextInt(Constant.PHRASES.length);
				
				//set the new instruction
				instructionIndex++;
				
				break;
			case Experiment.ON_BREAK:
				nextState = Experiment.INTER_TRIAL;
				resetBreak();
				break;
			case Experiment.INTER_TRIAL:
				nextState = Experiment.ON_TRIAL;
				break;
			case Experiment.ON_TRIAL:
				nextState = Experiment.POST_TRIAL;
				break;
			case Experiment.POST_TRIAL:
				//by default, continue to the next trial
				nextState = Experiment.INTER_TRIAL;
				
				//set the new phrase
				phraseIndex = phraseIndexRandomGen.nextInt(Constant.PHRASES.length);
				
				//set the new instruction
				instructionIndex++;
				
				//take a break after 5 phrases
				if ( wordsBeforeBreak == 1 ) {
					nextState = Experiment.ON_BREAK;
				}
				//finish all blocks
				else if ( instructionIndex == Constant.INSTRUCTIONS.length ) {
					nextState = Experiment.FINISHED;
				}
				else {
					wordsBeforeBreak--;
				}
			case Experiment.FINISHED:
				break;
		}
		
		this.STATE = nextState;
		
		return this.STATE;
	}
}

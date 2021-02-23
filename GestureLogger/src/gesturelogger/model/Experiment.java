package gesturelogger.model;

public class Experiment {
	//Trial order used in the current session
	public int [] ActiveWordOrder, ActiveWordsetOrder, ActiveInstructionOrder;
	private int pid;
	private String fou;
	private int exp_type;
	
	//index in the word list
	private int wordIndex;
	//index in the repetition list
	private int repetitionIndex;
	//index in the wordset list (only used when participants do all the wordsets)
	private int wordsetIndex;
	//index in the instruction list
	private int instructionIndex;
	//index of word-list order in each block of wordset (also represents the block number)
	private int wordOrder;
	//experiment state
	private int STATE;
	//experiment ID
	private String trialID;
	//counter for break time
	
	//total words per sub-session before break
	public static int wordsBeforeBreak = 4;
	
	//experiment state
	public static final int	PRACTICE = -1,
							INTER_TRIAL = 0,
							ON_TRIAL = 1,
							POST_TRIAL = 2,
							ON_BREAK = 3,
							FINISHED = 4,
							INTER_QUIZ = 5,
							ON_QUIZ = 6,
							POST_QUIZ = 7;
	
	public static final int ALLSET = 0,
							SUBSET = 1;
	
	
	//word-list: Latin-Square, size 12x12
	public static int[][] WORD_ORDER =
		    {
		        {0, 1, 11, 2, 10, 3, 9, 4, 8, 5, 7, 6}, //0
		        {1, 2, 0, 3, 11, 4, 10, 5, 9, 6, 8, 7},
		        {2, 3, 1, 4, 0, 5, 11, 6, 10, 7, 9, 8},
		        {3, 4, 2, 5, 1, 6, 0, 7, 11, 8, 10, 9}, //3
		        {4, 5, 3, 6, 2, 7, 1, 8, 0, 9, 11, 10},
		        {5, 6, 4, 7, 3, 8, 2, 9, 1, 10, 0, 11},
		        {6, 7, 5, 8, 4, 9, 3, 10, 2, 11, 1, 0}, //6
		        {7, 8, 6, 9, 5, 10, 4, 11, 3, 0, 2, 1},
		        {8, 9, 7, 10, 6, 11, 5, 0, 4, 1, 3, 2},
		        {9, 10, 8, 11, 7, 0, 6, 1, 5, 2, 4, 3}, //9
		        {10, 11, 9, 0, 8, 1, 7, 2, 6, 3, 5, 4},
		        {11, 0, 10, 1, 9, 2, 8, 3, 7, 4, 6, 5}
		    };
	
	//wordset-list: Latin-Square, size 3x12
	public static int[][] WORDSET_ORDER =
		    {
		        {0,1,2}, //0
		        {1,2,0},
		        {2,0,1},
		        {2,1,0}, //3
		        {0,2,1},
		        {1,2,0},
		        {0,1,2}, //6
		        {1,2,0},
		        {2,0,1},
		        {2,1,0}, //9
		        {0,2,1},
		        {1,2,0}
		    };
	
	//wordset-list: Latin-Square, size [3x4]x12
	public static int[][] WORDSET_ORDER2 =
		    {
		        {1,0,2,0,2,1,2,1,0,0,1,2}, //0
		        {2,1,0,1,0,2,0,2,1,1,2,0},
		        {0,1,2,2,1,0,1,2,0,2,0,1},
		        {2,0,1,0,1,2,1,2,0,0,2,1}, //3
		        {0,2,1,1,2,0,2,0,1,0,1,2},
		        {1,2,0,2,0,1,0,1,2,2,1,0},
		        {0,1,2,1,0,2,2,0,1,1,2,0}, //6
		        {1,0,2,2,1,0,0,2,1,2,0,1},
		        {2,1,0,0,1,2,1,0,2,0,2,1},
		        {1,2,0,2,0,1,0,1,2,1,0,2}, //9
		        {2,0,1,0,2,1,1,0,2,2,1,0},
		        {0,2,1,1,2,0,2,1,0,1,0,2}
		    };
	
	//instruction-list: Latin-Square, size 2x12
	public static int [][] INSTRUCTION_ORDER = 
			{
				{0,1,2},
				{0,2,1}
			};
	
	
	/**
	 * default constructor
	 * Set active trial order based on the participant ID
	 * @param pid
	 */
	public Experiment ( int pid, String fou, int exp_type ) {
		this.pid = pid;
		this.fou = fou;
		this.exp_type = exp_type;
		
		switch ( exp_type ) {
			case Experiment.ALLSET:
				wordOrder = (pid*3)%12;
				ActiveWordOrder = Experiment.WORD_ORDER[wordOrder];
				ActiveWordsetOrder = Experiment.WORDSET_ORDER[pid%12];
				break;
			case Experiment.SUBSET:
				ActiveWordOrder = Experiment.WORD_ORDER[pid%12];
				ActiveWordsetOrder = Experiment.WORDSET_ORDER2[pid%12];
				
				new Constant( 1 );
				break;
		}
		
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
	public String getWord () {
		return Constant.WORD_SET[ getWordset() ] [ ActiveWordOrder[wordIndex] ];
	}
	
	
	public String getInstruction () {
		return Constant.INSTRUCTION[ INSTRUCTION_ORDER[ ((pid%2)+(ActiveWordOrder[wordIndex]%2))%2 ][instructionIndex] ];
	}
	
	
	public int getWordset () {
		if ( exp_type == Experiment.ALLSET ) {
			return ActiveWordsetOrder[wordsetIndex];
		}
		else {
			return ActiveWordsetOrder[ ActiveWordOrder[wordIndex] ];
		}
	}
	
	
	public String getWordCharacteristic () {
		return Constant.WORD_CHARACTERISTIC[ ActiveWordOrder[wordIndex] ];
	}
	
	
	public int getRepetitionIndex () {
		return repetitionIndex;
	}
	
	
	public String getTrialID () {
		return "P"+pid + "-" + 
				fou.charAt(0) + "-" + 
				this.getInstruction().charAt(0) + "-" + 
				getWordset() + "-" +
				Constant.WORD_ID[ ActiveWordOrder[wordIndex] ] + "-" + 
				repetitionIndex;
	}
	
	
	private void resetBreak () {
		wordsBeforeBreak = 4;
	}
	
	
	public int next () {
		int nextState = -1;
		
		switch ( this.STATE ) {
			case Experiment.PRACTICE:
				nextState = Experiment.INTER_TRIAL;
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
				
				//updating the experiment setting
				//finish a trial, repeat it
				if ( repetitionIndex < (Constant.NBREPETITIONS-1) ) {
					nextState = Experiment.ON_TRIAL;
					repetitionIndex++;
				}
				//finish all trials for an instruction, continue to next instruction
				else if ( instructionIndex < (Constant.NBINSTRUCTIONS-1) ) {
					instructionIndex++;
					repetitionIndex = 0;
				}
				//finish all trials for all instructions, continue to next word
				else if ( wordIndex < (Constant.NBWORDS-1) ) {
					wordIndex++;
					wordsBeforeBreak--;
					instructionIndex = 0;
					repetitionIndex = 0;
					
					//take a break after 4 words
					if ( wordsBeforeBreak == 0 && wordsetIndex >= (Constant.NBBLOCKS-1) ) {
						nextState = Experiment.ON_BREAK;
					}
				}
				//finish a block of wordset, start from Trial 0 on Word 0 for the next block of wordset
				else if ( wordsetIndex < (Constant.NBBLOCKS-1) ) {
					wordsetIndex++;
					
					wordOrder++;
					ActiveWordOrder = Experiment.WORD_ORDER[wordOrder];
					
					wordIndex = 0;
					instructionIndex = 0;
					repetitionIndex = 0;
				}
				//finish all blocks
				else {
					nextState = Experiment.FINISHED;
				}
			case Experiment.FINISHED:
				break;
		}
		
		this.STATE = nextState;
		
		return this.STATE;
	}
}

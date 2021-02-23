package gesturelogger.view;

import gesturelogger.model.Constant;
import gesturelogger.model.Experiment;

public class Page {
	/* VARIABLE DECLARATIONS */
	private boolean hasKeyboard, instructions[], hasOutput;
	private String text [], buttonText, buttonTexts[], word;
	private char character;
	private int state;
	private int outputType;
	
	
	/**
	 * default constructor, do nothing
	 */
	public Page () {
		//do nothing
	}
	
	
	/**
	 * public constructor for break/quiz page
	 * @param trialCounter
	 */
	public void initBreak ( int trialCounter ) {
		switch ( Constant.EXPTYPE ) {
		case 1:
			this.text = new String [] { "You have done", trialCounter + " out of " + Constant.NBTRIALS + " trials.", "", "Feel free to take a short break!"  };
			break;
		case 2:
			this.text = new String [] { "The first session is done.", " ", "Feel free to take a short break!" };
			break;
		case 3:
			this.text = new String [] { "Quiz time!", " ", "Please write the word three times", "with the specified goal." };
			break;
		}
		this.hasKeyboard = false;
		this.hasOutput = false;
		this.buttonText = "NEXT";
		this.state = Experiment.ON_BREAK;
	}
	
	
	/**
	 * to initialize for the INTER-TRIAL page
	 * @param trialID
	 * @param instruction
	 */
	public void initInterTrial ( String trialID, String instruction ) {
		switch ( Constant.EXPTYPE ) {
		case 1:
			this.text = new String[] { trialID, "Draw the next 10 words as","",instruction,"","as possible." };
			break;
		case 2:
			if ( instruction.contains(";") ) {
				String tmp[] = instruction.split(";");
				this.text = new String[3+tmp.length];
				this.text[0] = trialID;
				this.text[1] = "Draw the phrase three times.";
				this.text[2] = " ";
				for (int i = 3; i<this.text.length; i++) {
					this.text[i] = tmp[i-3];
				}
			}
			else {
				this.text = new String[] { trialID, "Draw the phrase three times.","",instruction };
			}
			break;
		case 3:
			this.text = new String[] { trialID, "Write these five text messages","to your friend." };
			break;
		}
		this.hasKeyboard = false;
		this.hasOutput = false;
		this.buttonText = "NEXT";
		this.state = Experiment.INTER_TRIAL;
	}
	
	
	/**
	 * for the trial page
	 * @param trialID
	 * @param instruction
	 * @param word
	 */
	public void initTrial ( String trialID, String word, String instruction ) {
		this.word = word;
		this.text = new String[] { trialID, instruction, word };
		this.hasKeyboard = true;
		this.hasOutput = ( Constant.EXPTYPE == 1 ? false : true );
		this.outputType = ( Constant.EXPTYPE == 2 ? Constant.COLORED_OUTPUT : Constant.DYNAMIC_OUTPUT );
		this.buttonText = ( Constant.EXPTYPE == 2 ? "NEXT PHRASE" : null );
		this.state = Experiment.ON_TRIAL;
	}
	
	
	/**
	 * for the TRIAL page in Experiment 3
	 * @param trialID
	 * @param instruction
	 * @param word
	 */
	public void initTrial ( String trialID, int outputType, String txt ) {
		this.word = txt;
		this.text = new String[] { trialID, txt };
		this.hasKeyboard = true;
		this.hasOutput = true;
		this.outputType = outputType;
		this.buttonText = "CLEAR";
		this.state = Experiment.ON_TRIAL;
	}
	
	
	/**
	 * for the post-trial page
	 * @param word
	 * @param trialID
	 */
	public void initPostTrial ( String trialID, String word, int trialCounter ) {
		switch ( Constant.EXPTYPE ) {
		case 1:
			this.text = new String[]  { trialID, "Thanks!", " ", " ", "Do you think you just typed", "\""+word+"\"?", 
									( trialCounter == Constant.NBTRIALS ? "Finish" : +(trialCounter+1)+"/"+Constant.NBTRIALS ) };

			this.buttonTexts = new String[] { "NEXT", "Yes", "No", "Not sure" };
			break;
		case 2:
			this.text = new String[]  { trialID, "Thanks!", " ", "Evaluate the result:", " ", "\"I am satisfied with the result.\"", 
				( trialCounter == Constant.NBTRIALS ? "Finish" : +(trialCounter+1)+"/"+Constant.NBTRIALS ) };

			this.buttonTexts = new String[] { "NEXT" };
			break;
		case 3:
			this.text = new String[]  { trialID, "Great!",
					( trialCounter == Constant.NBTRIALS ? "Finish" : +(trialCounter+1)+"/"+Constant.NBTRIALS ) };

			this.buttonTexts = new String[] { "NEXT" };
			break;
		}
		this.hasKeyboard = false;
		this.hasOutput = false;
		this.state = Experiment.POST_TRIAL;
	}
	
	
	/**
	 * public constructor for the PRACTICE page in Experiment 1
	 * @param msg
	 * @param hasKeyboard
	 * @param buttonText
	 */
	public Page ( String msg, boolean hasKeyboard, String buttonText[] ) {
		this.text = msg.split(";");
		this.hasKeyboard = hasKeyboard;
		this.hasOutput = false;
		if ( buttonText != null && buttonText.length == 1 ) {
			this.buttonText = buttonText[0];
		}
		else {
			this.buttonTexts = buttonText;
		}
		this.state = Experiment.PRACTICE;
	}
	
	
	/**
	 * public constructor for DRAWING A CHARACTER in MockUp
	 * @param msg
	 * @param character
	 * @param buttonText
	 */
	public Page ( String msg, char character, String buttonText[] ) {
		this.text = msg.split(";");
		this.character = character;
		this.hasKeyboard = false;
		this.hasOutput = false;
		if ( buttonText != null && buttonText.length == 1 ) {
			this.buttonText = buttonText[0];
		}
		else {
			this.buttonTexts = buttonText;
		}
		this.state = Experiment.PRACTICE;
	}
	
	
	/**
	 * public constructor for the TUTORIAL page in Experiment 2 and PRACTICE in Experiment 3
	 * @param msg
	 * @param hasKeyboard
	 * @param words
	 * @param buttonText
	 */
	public Page ( String msg, boolean hasKeyboard, String word, String buttonText[], int outputType ) {
		this.text = msg.split(";");
		this.hasKeyboard = hasKeyboard;
		this.hasOutput = true;
		this.outputType = outputType;
		if ( word != null ) {
			this.word = word;
		}
		if ( buttonText != null && buttonText.length == 1 ) {
			this.buttonText = buttonText[0];
		}
		else {
			this.buttonTexts = buttonText;
		}
		this.state = Experiment.PRACTICE;
	}
	
	
	/**
	 * public constructor for the tutorial page with instructions layout in Experiment 2
	 * @param msg
	 * @param hasKeyboard
	 * @param buttonText
	 * @param instructions
	 */
	public Page ( String msg, boolean hasKeyboard, String buttonText[], boolean instructions[] ) {
		this.text = msg.split(";");
		this.hasKeyboard = hasKeyboard;
		this.hasOutput = false;
		if ( buttonText.length == 1 ) {
			this.buttonText = buttonText[0];
		}
		else {
			this.buttonTexts = buttonText;
		}
		this.instructions = instructions;
		this.state = Experiment.PRACTICE;
	}
	
	
	/**
	 * public constructor for the MOCK-UP page
	 * @param outputType
	 * @param buttonText
	 */
	public Page ( int outputType, String buttonText[] ) {
		//this.text = ( outputType == Constant.DYNAMIC_OUTPUT ? new String[]{"dynamic output"} : new String[]{"colored output"} );
		//this.text = ( outputType == Constant.DYNAMIC_OUTPUT ? new String[]{"dynamic output"} : new String[]{"colored output"} );
		this.hasOutput = true;
		this.hasKeyboard = true;
		this.outputType = outputType;
		if ( buttonText != null && buttonText.length == 1 ) {
			this.buttonText = buttonText[0];
		}
		else {
			this.buttonTexts = buttonText;
		}
		this.state = Experiment.PRACTICE;
	}
	
	
	/**
	 * to initialize the INTER-QUIZ page
	 * @param instruction
	 */
	public void initInterQuiz ( String instruction ) {
		this.text = new String[] { "QUIZ", "Write \"hello\" three times."," ","Make them all "+instruction+"." };
		this.hasKeyboard = false;
		this.hasOutput = false;
		this.outputType = Constant.DYNAMIC_OUTPUT;
		this.buttonText = "NEXT";
		this.state = Experiment.INTER_QUIZ;
	}
	
	
	/**
	 * to initialize the QUIZ page
	 * @param instruction
	 */
	public void initQuiz ( String instruction ) {
		this.text = new String[] { "QUIZ", "Make them all "+instruction+".", "hello" };
		this.word = "hello";
		this.hasKeyboard = true;
		this.hasOutput = true;
		this.outputType = Constant.DYNAMIC_OUTPUT;
		this.buttonText = "NEXT";
		this.state = Experiment.ON_QUIZ;
	}
	
	
	/**
	 * to initialize the QUIZ page
	 * @param instruction
	 */
	public void initPostQuiz () {
		this.text = new String[]  { "QUIZ", "That's it!", "" };
		this.buttonTexts = new String[] { "NEXT" };
		this.hasKeyboard = false;
		this.hasOutput = false;
		this.outputType = Constant.DYNAMIC_OUTPUT;
		this.state = Experiment.POST_QUIZ;
	}
	
	
	public int getState () {
		return state;
	}
	
	
	public int getOutputType () {
		return outputType;
	}
	
	
	public boolean hasKeyboard () {
		return hasKeyboard;
	}
	
	
	public boolean hasOutput () {
		return hasOutput;
	}
	
	
	public boolean [] getInstructions () {
		return instructions;
	}
	
	
	public String [] getText () {
		return text;
	}
	
	
	public String getButtonText () {
		return buttonText;
	}
	
	
	public String[] getButtonTexts () {
		return buttonTexts;
	}
	
	
	public String getWord () {
		return word;
	}
	
	
	public char getCharacter () {
		return character;
	}
}

package gesturelogger.model;

import java.util.HashMap;

public class Constant {
	// EXPERIMENT TYPE
    public static int EXPTYPE = 4;
	// EXPERIMENT TYPE (COPY)
    public static final int EXPTYPE_O = 4;
    // NUMBER OF BLOCKS IN THE EXPERIMENT
    public static int NBBLOCKS = ( EXPTYPE == 1 ? 3 : ( EXPTYPE == 2 ? 1 : 4 ) );
    // NUMBER OF PRACTICE BLOCK IN THE EXPERIMENT
    public static int NBPRACTICE = ( EXPTYPE == 1 ? 1 : 1);
    // NUMBER OF REPETITIONS PER BLOCK
    public static int NBREPETITIONS = ( EXPTYPE == 1 ? 10 : ( EXPTYPE == 2 ? 3 : 1 ));
    // NUMBER OF WORDS
    public static int NBWORDS = ( EXPTYPE == 1 ? 12 : 1);
    // NUMBER OF INSTRUCTIONS
    public static int NBINSTRUCTIONS = ( EXPTYPE == 1 ? 3 : ( EXPTYPE == 2 ? 10 : 5 ));
    
    // THE RADIUS OF CONTROL POINTS
    public static float CONTROLPOINT_RAD = 20;
	
	//type of output
	public static int STATIC_OUTPUT = 0, COLORED_OUTPUT = 1, DYNAMIC_OUTPUT = 2;
    
	//font creation
	public static CardinalSpline SPLINE = new CardinalSpline();
	public static HashMap <Character,CharacterPath> FONT, FONT_BASELINE;
	public static int FONT_HEIGHT = 750;
	public static float STROKE_WIDTH = 10;					//the font's spline thickness
    
    public static String[][] WORD_SET = {
    	{
	        "your", //0
	        "wax",
	        "lose",
	        "all", //3
	        "jazz",
	        "fill",
	        "queue", //6
	        "midnight",
	        "bracket",
	        "pepper", //9
	        "vaccine",
	        "loose"
	    },
    	{
	        "pure", //0
	        "vein",
	        "taxi",
	        "zoo", //3
	        "feel",
	        "mess",
	        "power", //6
	        "joking",
	        "headache",
	        "puree", //9
	        "queen",
	        "syllable"
	    },
    	{
	        "per", //0
	        "sigh",
	        "back",
	        "peer", //3
	        "fell",
	        "knee",
	        "query", //6
	        "exorcize",
	        "jewel",
	        "tweet", //9
	        "middle",
	        "arrive"
	    }
    };
    
    
    public static String [] PHRASES =
    	{
	    	"accompanied by an adult",
			"see you later alligator",
			"do not say anything",
			"toss the ball around",
			"he watched in astonishment",
			"drugs should be avoided",
			"rain rain go away",
			"sprawling divisions are bad",
			"we missed your birthday",
			"take a coffee break",
			"my favorite web browser",
			"batman wears a cape",
			"presidents drive good cars",
			"we accept personal checks",
			"machinery is complicated",
			"please keep it confidential",
			"the second largest country",
			"vanilla flavored ice cream",
			"the power of denial",
			"call for more details",
			"the stock exchange dipped",
			"rejection letters are bad",
			"the dreamers of dreams",
			"we park in driveways",
			"olympic athletes use drugs",
			"the living is easy",
			"very reluctant to enter",
			"bad for the environment",
			"universities are expensive",
			"tickets are very expensive",
			"houses are too expensive",
			"wishful thinking is fine",
			"chemical spill took forever",
			"with each step forward",
			"world population is growing",
			"please follow the guideline",
			"teaching services will help",
			"the cotton is high",
			"correct your diction",
			"your talk was inspiring",
			"he is shouting loudly",
			"observation was made",
			"the daring young man",
			"parties announce a merger",
			"be home before midnight",
			"stability of the nation",
			"for your information only",
			"we drive on parkways",
			"important for political party",
			"well connected with people",
			"I hate baking pies",
			"the children are playing",
			"earth quakes are predictable",
			"Canada has ten provinces",
			"victims deserve redress",
			"I watched blazing saddles",
			"buckle up for safety",
			"play it again Sam",
			"time to go shopping",
			"we went grocery shopping",
			"sign the withdrawal slip",
			"dashing through the snow",
			"we are having spaghetti",
			"relations are very strained",
			"stay away from strangers",
			"santa claus got stuck",
			"the most beautiful sunset",
			"you have my sympathy",
			"this system of taxation",
			"a most ridiculous thing",
			"love means many things",
			"we better investigate this",
			"he called seven times",
			"the proprietor was available",
			"that is very unfortunate",
			"have a good weekend",
			"peek out the window",
			"we have enough witnesses",
			"I agree with you",
			"breathing is difficult",
			"acceptable circumstances",
			"starlight and bubble",
			"fish are jumping",
			"frequently asked questions",
			"round robin scheduling",
			"information super highway",
			"protect your environment",
			"user friendly interface",
			"communicate through email",
			"nobody cares anymore"
    	};

    
    public static String [][][] SENTENCES = {
    	//NATURAL TEXT
    	{
	    	{"hey nice to meet you",
	    		"where are you from",
	    		"how is that country",
	    		"how does it look like and how is the food there",
	    		"i'll visit one day"},
	    	{"you never know in advance",
	    		"it's the great mystery of our doctoral planet",
	    		"some wise and old people say there is a logic in them",
	    		"they may be old and crazy people",
	    		"do you also believe in forms"},
	    	{"yes that's the sound",
	    		"i am really surprised",
	    		"they take all data and send it directly to nsa",
	    		"so let's not talk about drugs",
	    		"drugs to pay for weapons used in terrorism"},
	    	{"you really like zoos",
	    		"there is one somewhere near Paris",
	    		"you can be in a cage",
	    		"and the animals are free",
	    		"there are zoos like this in India"},
	    	{"sounds like spice though",
	    		"i had that too yesterday",
	    		"i ate such a horrible dish",
	    		"someone told me they give that to dogs",
	    		"in Netherlands that is not given to humans any longer"},
	    	{"i went to the vegan burger shop",
	    		"so nice to have burger after six months",
	    		"i tried the mustard sauce and almost cried",
	    		"but i'm so glad that they have vegetarian stuff",
	    		"i'm saving two hours by not cooking"},
	    	{"is it weird to get used to",
	    		"you used iPhone i assume",
	    		"did you find it",
	    		"if we're using the same keyboard",
	    		"the question mark is where the numbers are"},
	    	{"cool so you are doing an internship here",
	    		"I'm first year of my PhD",
	    		"studying about virtual reality",
	    		"and i belong to another lab in this campus",
	    		"we have weird weather today"},
	    	{"most of the times we somehow end up discussing how you perceive me",
	    		"it doesn't seem like i'm living up to your standards",
	    		"my mom loves me",
	    		"can't do no wrong in her eyes",
	    		"she's the best"},
	    	{"i don't have multiple lovers",
	    		"not popular like you",
	    		"was tempted to say this is sexist",
	    		"but i actually don't know",
	    		"because i don't actually know what i'm talking about"},
	    	{"are we talking about meat",
	    		"i like it barbecued",
	    		"you don't need to eat it raw to feel it tender",
	    		"and the flavor is also special in a good way",
	    		"you should come to Argentina"},
	    	{"i've also been to see Brooklyn this weekend",
	    		"and then to this really cool place",
	    		"it's like a bar club live music cafe",
	    		"and on sunday it was her birthday",
	    		"so we went for Chinese hotpot"}
    	},
    	//PRESCRIBED TEXT
    	{	//headache, midnight, pepper, back
    		//there, and, from, then
    		{"i usually suffer from headaches just before midnight",
        		"so i add anti inflammatory foods to my morning tea",
        		"there is ginger and turmeric and black pepper",
        		"i sit back and relax while enjoying the warm drink",
        		"i feel better afterwards"},
        	//vein, knee, lose, per
        	//there, and, it's
        	{"i noticed little purple patches behind my knee",
        		"no one is exactly sure why people get varicose vein",
        		"but there are definite risk factors",
        		"our blood vessels lose elasticity as we get older",
        		"it’s better to exercise per week and avoid pressure on the legs"},
        	//fell, queen, power, jewel
        	//how, and, its, that
        	{"how dutch fell in love with their new queen",
        		"she has respect and was willing to make an effort to understand",
        		"she knows her place by the king's side",
        		"she wore a pale rose colored dress and a grand jewel",
        		"and that she knows how to utilise its power of expression"},
        	//arrive, middle, mess, taxi
        	//and, then, there, that
        	{"we took a taxi from the airport",
        		"we arrived to the hotel in the middle of the night",
        		"there was a problem that they didn't put our reservation",
        		"once the mess was clean we went for dinner",
        		"and then we went for a drink"},
        	//sigh, vaccine, queue, all
        	//that, it's, and
        	{"there has been a shortage of yellow fever vaccine",
        		"it's because of an interruption and a breakdown in equipment",
        		"but now all travelers can breathe a sigh of relief",
        		"the ministry announced that the supplies had arrived",
        		"people queue up as the stocks return"},
        	//jazz, loose, peer, feel
        	//that, then, and
        	{"i feel that jazz education became seemingly fractured",
        		"they just invited the best musicians they knew",
        		"and then set them loose to teach",
        		"i heard students play in front of their peer",
        		"every one of them had a different sound and approach"},
        	//fill, bracket, tweet, your
        	//those, there, that
        	{"always wait until the last minute to fill out a bracket",
        		"you have a whole week to prepare",
        		"why rush it when you can wait",
        		"speaking of your bets there is no shame in filling out multiple ones",
        		"those who complain that you tweet too much are terrible"},
        	//pure, query, joking
        	//that, and
        	{"i hope that you're joking",
        		"a traditional layout does not work on a mobile and a desktop",
        		"users don't expect to see the same thing",
        		"developing a pure mobile only version costs more",
        		"you can’t just apply a media query"},
        	//zoo, wax, back
        	//that, this, and, then
        	{"this machine is usually found in a zoo",
        		"it molds a plastic figure that you can take back as a souvenir",
        		"some use wax instead of plastic",
        		"the mold goes together and a hot plastic figure is revealed",
        		"it's very exciting for kids"},
        	//syllable, feel, middle
        	//that, how, it's
        	{"i teach middle schoolers to read music notes",
        		"the most popular is the number counting of beats",
        		"i was trained that way myself",
        		"but some of the same syllables were also used to count triplets",
        		"so it's better to feel the rhythms instead"},
        	//puree, pepper
        	//and, this, that
        	{"a red bell pepper is loaded with folate and vitamins",
        		"when paired with a white potato it has a creamy sweet taste",
        		"and look at that amazing bright red color",
        		"how yummy does that look",
        		"i left this puree plain without any spices"},
        	//queue, midnight, feel
        	{"thousands of fans queue at midnight to see the new film",
        		"some of them even dressed up for the occasion",
        		"i was there with three of my friends",
        		"my excitement had been building up for months",
        		"i feel like a kid again"}
    	}
    };
    
    //INSTRUCTION in Experiment 1
    public static String[] INSTRUCTION = new String []
    	{
    		"accurately",
    		"quickly",
    		"creatively"
    	};
    
    
    //INSTRUCTION in Experiment 2
    public static String[] INSTRUCTIONS = new String []
    	{
    		"Try to make each phrase;the same color of black",
    		"Try to make each phrase;a different shade of green",
    		"Try to make each phrase have 2 colors;use as many colors as you can",
    		"Scribble on the letter key(s)",
    		"Draw the phrase at different speeds",
    		"Go outside of the keyboard",
    		"Draw for 1) your partner;2) your boss, 3) a stranger",
    		"Draw for 1) your niece;2) your best friend, 3) your parent",
    		"Express how you feel;1) happy, 2) frustrated, 3) sad",
    		"Express how you feel;1) angry, 2) busy, 3) bored"
    	};
    
    //INSTRUCTION in Experiment 3
    public static String[] QUIZ_INSTRUCTION = new String []
    	{
    		"bold and red",
    		"italic and contain blue",
    		"green"
    	};
    
    // NUMBER OF TRIALS
    public static int NBTRIALS = NBINSTRUCTIONS * NBREPETITIONS * NBWORDS;

    //WORD DETAILS in Experiment 1
    public static String[] WORD_ID =
    	{
	        "S1Z", //0
	        "S1A",
	        "S1O",
	        "S2Z", //3
	        "S2A",
	        "S2O",
	        "L1Z", //6
	        "L1A",
	        "L1O",
	        "L2Z", //9
	        "L2A",
	        "L2O",
	    };

    //WORD DETAILS in Experiment 1
    public static String[] WORD_CHARACTERISTIC =
    	{
	        "SHORT,1R,ZERO", //0
	        "SHORT,1R,ACUTE",
	        "SHORT,1R,OBTUSE",
	        "SHORT,2R,ZERO", //3
	        "SHORT,2R,ACUTE",
	        "SHORT,2R,OBTUSE",
	        "LONG,1R,ZERO", //6
	        "LONG,1R,ACUTE",
	        "LONG,1R,OBTUSE",
	        "LONG,2R,ZERO", //9
	        "LONG,2R,ACUTE",
	        "LONG,2R,OBTUSE",
	    };

    //TRIAL DETAILS in Experiment 2
    public static String[] TRIAL_ID =
    	{
	        "OC", //0
	        "OD",
	        "OV",
	        "IC", //3
	        "ID",
	        "IV",
	        "HP1", //6
	        "HP2",
	        "HE1",
	        "HE2"
	    };

    //TRIAL DETAILS in Experiment 2
    public static String[] TRIAL_DETAIL =
    	{
	        "OUTPUT,CONSISTENT", //0
	        "OUTPUT,DIFFERENT",
	        "OUTPUT,VARIED",
	        "INPUT,CONSISTENT",
	        "INPUT,DIFFERENT",
	        "INPUT,VARIED", //5
	        "HUMAN,RECIPIENT",
	        "HUMAN,RECIPIENT",
	        "HUMAN,EMOTION",
	        "HUMAN,EMOTION"
	    };
    
    
    
    public Constant ( int blockNo ) {
    	this();
    	Constant.NBBLOCKS = blockNo;
    }
        
        
	public Constant () {
    	NBTRIALS = NBINSTRUCTIONS * NBREPETITIONS * NBWORDS * NBBLOCKS;
    	FONT = new HashMap <Character,CharacterPath>(26);
    	FONT_BASELINE = new HashMap <Character,CharacterPath>(26);
    }
    
}

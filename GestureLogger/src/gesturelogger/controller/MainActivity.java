 package gesturelogger.controller;

import gesturelogger.controller.SettingActivity;
import gesturelogger.model.*;
import gesturelogger.view.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.example.gesturelogger.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {
	/* VARIABLE DECLARATIONS */
	private PromptFragment promptFragment = new PromptFragment();
	private PracticeFragment practiceFragment;
	private ExperimentFragment experimentFragment;
	private WOZKeyboard keyboard;
	public float width;
	public float height;
	
	//experiment
	private Experiment experiment;
	private ExperimentTwo experiment2;
	private ExperimentThree experiment3;
	private int PID, FID;
	private String FOU;
	
	//logging
	private PrintWriter pwC, pw;
	private FileOutputStream fC, f;
	private StringBuilder log = new StringBuilder(), coordinate = new StringBuilder(), logDB = new StringBuilder();
	private String logFile, coordinateFile, dbFile;
	private File dir, dirDB, fileC, file, fileDB;
	private int logCounter = 0;
	private boolean hasBaselineFont = false;
	
	//preference
	private boolean isAppliedToAll, isColoredOutput, isDynamicOutput;
	private int thresholdRGB [] = { 255, 255, 255 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView( R.layout.fragment_container );
        
        FragmentManager fm = getFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
//        if ( Constant.EXPTYPE == 1 ) {
//	        //prompt participant ID, expertise level, and device type used in the experiment
//	    	ft.replace(R.id.root, promptFragment);
//        }
//        else if ( Constant.EXPTYPE == 2 ) {
//        	//prompt participant ID, expertise level, and device type used in the experiment
//        	practiceFragment = new PracticeFragment();
//	    	ft.replace(R.id.root, practiceFragment);
//        }
    	ft.replace(R.id.root, promptFragment);
    	ft.commit();
        
    	//set width and height of the screen
    	Display display = getWindowManager().getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);
    	width = size.x;
    	height = size.y;
    }
    

   @Override
    public void onDestroy() {
        super.onDestroy();
        
        //set EXPTYPE to default value
        if ( Constant.EXPTYPE != Constant.EXPTYPE_O ) {
        	Constant.EXPTYPE = Constant.EXPTYPE_O;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu FOR MOCKUP
    	//this adds items to the action bar if it is present.
    	if ( Constant.EXPTYPE == 4 ) {
    		getMenuInflater().inflate(R.menu.main, menu);
			
			this.setPreference();
    	}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Intent i = new Intent(this, SettingActivity.class);
			startActivityForResult(i, 1);
			
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//set preference
    	this.setPreference();
	}
    
    
    /**
     * to set value of preference on color and dynamic font
     */
    private void setPreference () {
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//get values
    	this.isAppliedToAll = sp.getBoolean("all",false);
    	this.isColoredOutput = sp.getBoolean("color", false);
    	this.isDynamicOutput = sp.getBoolean("font", false);
    	this.thresholdRGB[0] = sp.getInt("red", 255);
    	this.thresholdRGB[1] = sp.getInt("green", 255);
    	this.thresholdRGB[2] = sp.getInt("blue", 255);
    	
    	System.out.println( "PREF " + sp.getInt("red", 255) + " " + sp.getInt("green", 255) + " " + sp.getInt("blue", 255) );
    }


	public void startPractice () {
        //initiate constant
        new Constant();
        
    	//set PID
    	PID = promptFragment.getPID();
    	FOU = promptFragment.getFrequencyOfUse();
    	FID = promptFragment.getFID();
    	
    	//set experiment setup
    	switch ( Constant.EXPTYPE ) {
    	case 1:
    		experiment = new Experiment( PID, FOU, Experiment.SUBSET);//ALLSET );
    		break;
    	case 2:
    		experiment2 = new ExperimentTwo( PID, FOU );
    		break;
    	case 3:
    		experiment3 = new ExperimentThree( PID, FOU );
    		setDatabaseDirectory();
    		break;
    	case 30:
    		setDatabaseDirectory();
    		break;
    	case 4:
    		Constant.EXPTYPE = PID;
    		setDatabaseDirectory();
    		break;
    	}
    	
    	//change view to start
    	practiceFragment = new PracticeFragment();
    	
    	FragmentManager fm = getFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	ft.setCustomAnimations( android.R.animator.fade_in, android.R.animator.fade_out );
    	ft.replace(R.id.root, practiceFragment);
    	ft.commit();
    }
    
    
    public void startExperiment () {
    	setLogDirectory();
    	
    	//change view to start
    	experimentFragment = new ExperimentFragment();
    	
    	FragmentManager fm = getFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	ft.setCustomAnimations( android.R.animator.fade_in, android.R.animator.fade_out );
    	ft.replace(R.id.root, experimentFragment);
    	ft.commit();
    }
    
    
    public int getState () {
    	switch ( Constant.EXPTYPE ) {
    	case 1:
    		return experiment.getCurrentState();
    	case 2:
    		return experiment2.getCurrentState();
    	case 3:
    		return experiment3.getCurrentState();
    	}
    	return -1;
    }
    
    
    /**
     * to get the output type based on user's preference
     * -- ONLY used in mock-up
     * @return output-type code
     */
    public int getOutputPreference () {
    	if ( !this.isColoredOutput ) {
    		return Constant.STATIC_OUTPUT;
    	}
    	else if ( this.isDynamicOutput ) {
    		return Constant.DYNAMIC_OUTPUT;
    	}
    	else {
    		return Constant.COLORED_OUTPUT;
    	}
    }
    
    
    public int[] getRGBThreshold () {
    	return thresholdRGB;
    }
    
    
    /**
     * @return boolean whether all output should use the same output-type
     */
    public boolean isAppliedToAll () {
    	return this.isAppliedToAll;
    }
    
    
    public void next () {
    	int nextState;
    	switch ( Constant.EXPTYPE ) {
    	case 1:
        	nextState = experiment.next();
        	if ( nextState == Experiment.FINISHED ) {
        		experimentFragment.finish();
        	}
        	else {
        		experimentFragment.runExperiment(experiment.getWord(), experiment.getInstruction(), experiment.getTrialID(), nextState);
        	}
        	break;
        	
    	case 2:
        	nextState = experiment2.next();
        	if ( nextState == Experiment.FINISHED ) {
        		experimentFragment.finish();
        	}
        	else {
        		experimentFragment.runExperiment(experiment2.getPhrase(), experiment2.getInstruction(), experiment2.getTrialID(), nextState);
        	}
        	break;
        	
    	case 3:
    		nextState = experiment3.next();
        	if ( nextState == Experiment.FINISHED ) {
        		experimentFragment.finish();
        	}
        	else if ( nextState == Experiment.PRACTICE ) {
        		experimentFragment.runExperiment(experiment3.getSentence(), null, 
        				experiment3.getTrialID(), nextState, experiment3.getPracticeIndex());
        	}
        	else {
        		experimentFragment.runExperiment(experiment3.getSentence(), null, 
        				experiment3.getTrialID(), nextState, experiment3.getCurrentOutputType());
        	}
    		break;
    	}
    }
    
    public void createKeyboard () {
    	//create a model object of the WOZ keyboard
		keyboard = new WOZqwerty();
		
		if ( experimentFragment == null ) {
        	practiceFragment.initKeyboard( keyboard );
		}
		else {
			experimentFragment.initKeyboard( keyboard );
		}
    }
    
    
    /**
     * to load defined font from an existing file
     * @param line
     */
    private void loadFont ( String line, boolean isBaseline ) {
    	String [] tmp = line.split(";"), tmp2;
    	
    	CharacterPath cp = new CharacterPath( tmp[0].charAt(0), Integer.parseInt(tmp[3]), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]) );
    	for ( int i=4; i<tmp.length; i++ ) {
    		tmp2 = tmp[i].split(",");
    		for (int j=0; j<tmp2.length; j=j+2) {
    			cp.draw( Float.parseFloat(tmp2[j]), Float.parseFloat(tmp2[j+1]) );
    		}
    		
    		//detach if there's another spline
    		if ( i+1 != tmp.length ) {
    			cp.detach();
    		}
    	}
    	if ( isBaseline ) {
    		Constant.FONT_BASELINE.put( tmp[0].charAt(0) , cp);
    	}
    	else {
    		Constant.FONT.put( tmp[0].charAt(0) , cp);
    	}
    }
    
    
    public boolean hasBaselineFont () {
    	return hasBaselineFont;
    }
    
    
    public void setLogDirectory () {
    	String fname = "P" + PID + "_" + FOU;
    	
		Date date = new Date();
    	SimpleDateFormat dateFormat = new SimpleDateFormat( "MM.dd HH:mm" );
    	
    	File root = android.os.Environment.getExternalStorageDirectory(); 

        dir = new File ( root.getAbsolutePath() + "/GestureLogger/" + fname + "_" + dateFormat.format(date) );
        dir.mkdirs();
        Log.w("directory","\nExternal file system: "+dir.getAbsolutePath());
        
        logFile = fname + ".csv";
        coordinateFile = fname + "[gestures].csv"; 
    }
    
    
    public void logCoordinate ( String coord ) {
    	if ( coordinate.length() != 0 ) {
    		coordinate.append('\n');
    	}
    	
    	switch ( Constant.EXPTYPE ) {
    	case 1:
        	SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss.SSS" );
        	
    		//(word),(instruction),(repetition index),(timestamp),(touch type),(coordX;coordY)
        	coordinate.append(experiment.getWord()).append(',').append(experiment.getInstruction()).append(',').append(experiment.getRepetitionIndex());
        	coordinate.append(',').append(dateFormat.format(new Date())).append(',').append(coord);
        	break;
    	case 2:
    		//(PID),(output/input),(consistent/different/varied),(word),(trialNo),(repNo),(timestamp),(coordX),(coordY)
    		coordinate.append('P').append(PID).append(',').append(experiment2.getTrialDetail()).append(',');
    		coordinate.append(experiment2.getPhrase()).append(',').append(coord);
    		break;
    	case 3:
    		coordinate.append('P').append(PID).append(',').append(this.FOU).append(',');
    		coordinate.append(experiment3.getTrialDetail()).append(',').append(coord);
    		break;
    	}
    	
    	logCounter++;
        try {
        	//set buffer size to be 500
        	//push only when the buffer is full
        	if ( logCounter % 500 == 0 ) {
        		Log.w("LOG IT!",logCounter+"");
		    	fileC = new File( dir, coordinateFile );
		    	fC = new FileOutputStream(fileC);
		    	pwC = new PrintWriter(fC);
		        pwC.println( coordinate.toString() );
		        pwC.flush();
		        pwC.close();
				fC.close();
        	}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * push the remaining data in the buffer
     */
    public void logCoordinate () {
    	try {
    		Log.w("LOG IT!",logCounter+"");
	    	fileC = new File( dir, coordinateFile );
	    	fC = new FileOutputStream(fileC);
	    	pwC = new PrintWriter(fC);
	        pwC.println( coordinate.toString() );
	        pwC.flush();
	        pwC.close();
			fC.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void log ( String tag ) {
    	if ( log.length() != 0 ) {
    		log.append('\n');
    	}
    	
    	switch ( Constant.EXPTYPE ) {
    	case 1:
    		//(PID),(FOU),(wordset),(short/long),(1/2 letter repetition),(zero/acute/obtuse),(word),(instruction),(repetition index),(confidence level)
    		log.append('P').append(PID).append(',').append(FOU).append(',').append("WS").append(',').append(experiment.getWordset()).append(',');
    		log.append(experiment.getWordCharacteristic()).append(',').append(experiment.getWord()).append(',').append(experiment.getInstruction()).append(',').append(experiment.getRepetitionIndex()).append(',').append(tag);
        break;
    	case 2:
    		//(PID),(FOU),(output/input),(consistent/different/varied),(word),(trialNo),(totalRep),(meanAngularity),(meanSize),(meanSpeedRatio),(red),(green),(blue),(correctRate),(satisfactionRate)
    		log.append('P').append(PID).append(',').append(FOU).append(',').append(experiment2.getTrialDetail()).append(',').append(experiment2.getPhrase()).append(',').append(tag);
            break;
    	case 3:
    		//(PID),(FOU),(natural/prescribed/quiz-instruction),(dynamic/static),(trialNo),(word),(r),(sizeRatio),(g),(angularity),(b),
    		//(speedRatio),(speed),(isIntendedWord/not)
    		log.append('P').append(PID).append(',').append(FOU).append(',').append(experiment3.getTrialDetail()).append(',').append(tag);
            Log.w("log",tag);
    		break;
    	}

        try {
        	file = new File( dir, logFile );
        	f = new FileOutputStream(file);
            pw = new PrintWriter(f);
            pw.println( log.toString() );
            pw.flush();
            pw.close();
			f.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }   
    }
    
    
    public void setDatabaseDirectory () {
    	String fname = "F" + FID;
    	
    	File root = android.os.Environment.getExternalStorageDirectory(); 

        dirDB = new File ( root.getAbsolutePath() + "/GestureLogger/DB" );
        dirDB.mkdirs();
        Log.w("directory","\nExternal file system DB: "+dirDB.getAbsolutePath());
        
        dbFile = fname + ".db";
        
        //check if the font with the specific FID exists
        File file = new File(dirDB, dbFile);
        //if yes, then load the data
        if ( file.exists() ) {
        	try {
        	    BufferedReader br = new BufferedReader(new FileReader(file));
        	    String line;

        	    while ((line = br.readLine()) != null) {
        	        if ( !line.isEmpty() ) {
        	        	//variation
        	        	loadFont( line, false );
        	        	
        	        	//based (as the default value)
        	        	loadFont( line, true );
        	        }
        	    }
        	    
        	    br.close();
        	}
        	catch (IOException e) {
        	    e.printStackTrace();
        	}
        }
        
        //check if the baseline for the font with the specific FID exists
        file = new File(dirDB, fname + "b.db");
        //if yes, then load the data
        if ( file.exists() ) {
        	hasBaselineFont = true;
        	
        	try {
        	    BufferedReader br = new BufferedReader(new FileReader(file));
        	    String line;

        	    while ((line = br.readLine()) != null) {
        	        if ( !line.isEmpty() ) {
        	        	loadFont( line, true );
        	        }
        	    }
        	    
        	    br.close();
        	}
        	catch (IOException e) {
        	    e.printStackTrace();
        	}
        }
        else {
        	hasBaselineFont = false;
        }
    }
    
    
    public void duplicateCharacterAsBaseline () {
    	for ( int i='a'; i<='z'; i++) {
    		logCharacter( Constant.FONT.get((char)i).toString(), true );
    	}
    }
    
    
    public void logCharacter ( char lastChar ) {
    	for ( int i='a'; i<=lastChar; i++) {
    		logCharacter( Constant.FONT.get((char)i).toString(), false );
    	}
    }
    
    
    public void logCharacter ( String tag, boolean isBaseline ) {
    	if ( logDB.length() != 0 ) {
    		logDB.append('\n');
    	}
    	
    	logDB.append(tag);
    	
    	try {
        	fileDB = new File( dirDB, ( isBaseline ? "F" + FID + "b.db" : dbFile ) );
        	f = new FileOutputStream(fileDB);
            pw = new PrintWriter(f);
            pw.println( logDB.toString() );
            pw.flush();
            pw.close();
			f.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }   
    }
    
    
    public void screenshot ( View view ) {
    	view.setDrawingCacheEnabled(true);
    	
    	try {
    		switch ( Constant.EXPTYPE ) {
    		case 1:
    			f = new FileOutputStream( new File( dir, "P" + PID + "_" + experiment.getWord() + "_" + experiment.getInstruction().charAt(0) + "_" + experiment.getRepetitionIndex() +".png" ) );
    			view.getDrawingCache().compress( Bitmap.CompressFormat.PNG, 100, f );
    			break;
    		case 2:
    			f = new FileOutputStream( new File( dir, experiment2.getTrialID() +".png" ) );
        		view.getDrawingCache().compress( Bitmap.CompressFormat.PNG, 100, f );
        		break;
    		case 3:
    			f = new FileOutputStream( new File( dir, experiment3.getTrialID() +".png" ) );
        		view.getDrawingCache().compress( Bitmap.CompressFormat.PNG, 100, f );
    		}
    		f.flush();
    		f.close();
    	}
    	catch( Exception e ) {
    		e.printStackTrace();
    	}

    	view.destroyDrawingCache();
    }



	public EditText showKeyboard() {
		//((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
		EditText editText;
		if ( experimentFragment == null ) {
			editText = practiceFragment.addTextfield();
		}
		else {
			editText = experimentFragment.addTextfield();
		}
		
		editText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
		
		return editText;
	}
	
	
	public Button showButton ( String text ) {
		if ( experimentFragment == null ) {
			return practiceFragment.addButton(text);
		}
		else {
			return experimentFragment.addButton(text);
		}
	}
	
	
	public void hideKeyboard ( View view ) {
		this.hideKeyboard( view, null );
	}
	
	
	public void hideKeyboard ( View view, View viewToHide ) {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    
		if ( viewToHide != null && experimentFragment == null ) {
        	practiceFragment.removeView(viewToHide);
		}
		else if ( viewToHide != null ) {
			experimentFragment.removeView(viewToHide);
		}
	}

}

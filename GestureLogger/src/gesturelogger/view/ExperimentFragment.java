package gesturelogger.view;

import gesturelogger.controller.MainActivity;
import gesturelogger.model.Constant;
import gesturelogger.model.WOZKeyboard;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.gesturelogger.R;

public class ExperimentFragment extends Fragment {
	/* VARIABLE DECLARATIONS */
	protected MainActivity parentController;
	protected View view;
	protected DrawingView canvas;
	
	
	@Override
	public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		switch ( Constant.EXPTYPE ) {
		//for Experiment 1
		case 1:
			//create and activate the DrawingView
			view = inflater.inflate(R.layout.experiment, null);
			canvas = (ExperimentView) view.findViewById(R.id.canvas);
			break;
		case 2:
			//create and activate the DrawingView
			view = inflater.inflate(R.layout.experimenttwo, null);
			canvas = (ExperimentTwoView) view.findViewById(R.id.canvas);
			break;
		case 3:
			//create and activate the DrawingView
			view = inflater.inflate(R.layout.experimentthree, null);
			canvas = (ExperimentThreeView) view.findViewById(R.id.canvas);
			break;
		}
		
		//get fragment parent object
		parentController = (MainActivity) getActivity();
		
		parentController.createKeyboard();
		
		//tell parent controller that the fragment is ready to run the experiment
		parentController.next();
		
		return view;
	}
	
	
	public void initKeyboard ( WOZKeyboard keyboard ) {
		canvas.initKeyboard( keyboard );
	}
	
	
	public void runExperiment ( String word, String instruction, String trialID, int state ) {
		canvas.setExperimentParameter( word, instruction, trialID, state );
	}
	
	
	/**
	 * for experiment 3 where you can choose the type of output
	 * @param word
	 * @param instruction
	 * @param trialID
	 * @param state
	 */
	public void runExperiment ( String word, String instruction, String trialID, int state, int outputType ) {
		canvas.setExperimentParameter( word, instruction, trialID, state, outputType );
	}


	public void finish() {
		canvas.finish();
	}


	public EditText addTextfield() {
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout);
		
		//add text field
		EditText textfield = new EditText(parentController);
		textfield.setTextSize(10);
		textfield.setSingleLine(true);
		textfield.setAlpha(0);
		textfield.requestFocus();
		RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(
				(int)parentController.width-250, 
			    LayoutParams.WRAP_CONTENT);
		l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		l.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layout.addView( textfield, l );

		return textfield;
	}
	
	
	public Button addButton ( String text ) {
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout);
		
		//add text field
		Button b = new Button(parentController);
		b.setText(text);
		b.setSingleLine(true);
		//textfield.setAlpha(0);
		b.requestFocus();
		RelativeLayout.LayoutParams l = new RelativeLayout.LayoutParams(
				250, 
			    LayoutParams.WRAP_CONTENT);
		l.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		l.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layout.addView( b, l );

		return b;
	}


	public void removeView( View v ) {
		// Get scroll view out of the way
		//((ViewGroup)layout.getParent()).removeView(v);
		v.setVisibility(View.GONE);
	}
}

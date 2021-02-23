package gesturelogger.view;

import gesturelogger.controller.MainActivity;
import gesturelogger.model.*;

import com.example.gesturelogger.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class PracticeFragment extends Fragment {
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
			view = inflater.inflate(R.layout.practice, null);
			canvas = (PracticeView) view.findViewById(R.id.canvas);
			break;
		case 2:
			//create and activate the DrawingView
			view = inflater.inflate(R.layout.tutorial, null);
			canvas = (TutorialView) view.findViewById(R.id.canvas);
			break;
		case 3:
			//create and activate the DrawingView
			view = inflater.inflate(R.layout.briefing, null);
			canvas = (BriefingView) view.findViewById(R.id.canvas);
			break;
		case 30:
			//create and activate the DrawingView
			view = inflater.inflate(R.layout.fontcreator, null);
			canvas = (FontCreatorView) view.findViewById(R.id.canvas);
			break;
		case 4:
			view = inflater.inflate(R.layout.mockup, null);
			canvas = (MockupView) view.findViewById(R.id.canvas);
			break;
		}
		
		//get fragment parent object
		parentController = (MainActivity) getActivity();
		
		parentController.createKeyboard();
		
		return view;
	}
	
	
	public void initKeyboard ( WOZKeyboard keyboard ) {
		canvas.initKeyboard( keyboard );
	}
	
	
	public EditText addTextfield () {
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout);
		
		//add text field
		EditText textfield = new EditText(parentController);
		textfield.setTextSize(9);
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

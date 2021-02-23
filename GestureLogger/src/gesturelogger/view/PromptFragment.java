package gesturelogger.view;

import gesturelogger.controller.MainActivity;
import gesturelogger.model.Constant;

import com.example.gesturelogger.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class PromptFragment extends Fragment {
	/* VARIABLE DECLARATIONS */
	private MainActivity parentController;
	private View view;
	private Button startButton;
	private int PID = 0, FID = 0;
	private String FREQUENCY_OF_USE = "NON";//, DEVICE;
	
	@Override
	public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		switch ( Constant.EXPTYPE ) {
		//for Experiment 1 & 2
		case 1: case 2:
			view = inflater.inflate(R.layout.prompt, null);
			
			//components from prompt.xml
	        startButton = (Button) view.findViewById(R.id.startButton);
	        startButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//get participant ID
					EditText name = (EditText) view.findViewById(R.id.name);
					PID = ( name.getText().toString().isEmpty() ? 1 : Integer.parseInt( name.getText().toString() ) );
					
					//get expertise level
					RadioButton novice = (RadioButton) view.findViewById(R.id.novice);
					RadioButton occasional = (RadioButton) view.findViewById(R.id.occasional);
					FREQUENCY_OF_USE = (  novice.isChecked() ? "NON" : ( occasional.isChecked() ? "OCCASIONAL" : "FREQUENT" ) );
			        
			        //change layout
			        parentController.startPractice();
			        startButton.setOnClickListener(null);
				}
	        });
	        break;
	    //for font creation and Experiment 3
		case 3: case 30:
			view = inflater.inflate(R.layout.font_prompt, null);
			
			//components from font_prompt.xml
	        startButton = (Button) view.findViewById(R.id.startButton);
	        startButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//get participant ID
					EditText name = (EditText) view.findViewById(R.id.name);
					PID = ( name.getText().toString().isEmpty() ? 1 : Integer.parseInt( name.getText().toString() ) );
					
					//get expertise level
					RadioButton novice = (RadioButton) view.findViewById(R.id.novice);
					RadioButton occasional = (RadioButton) view.findViewById(R.id.occasional);
					FREQUENCY_OF_USE = (  novice.isChecked() ? "NON" : ( occasional.isChecked() ? "OCCASIONAL" : "FREQUENT" ) );
					
					//get font ID
					EditText font = (EditText) view.findViewById(R.id.fontID);
					FID = ( font.getText().toString().isEmpty() ? 78 : Integer.parseInt( font.getText().toString() ) );
					
			        //change layout
			        parentController.startPractice();
			        startButton.setOnClickListener(null);
				}
	        });
			
			break;
		//for mock-up
		case 4: case 5:
			//FID = 78;
	        //((MainActivity) getActivity()).startPractice();
			
			view = inflater.inflate(R.layout.mockup_prompt, null);
			
			//components from font_prompt.xml
	        startButton = (Button) view.findViewById(R.id.startButton);
	        startButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//get type
					RadioButton mockup = (RadioButton) view.findViewById(R.id.mockup);
					PID = (  mockup.isChecked() ? 4 : 30 );
					
					//get font ID
					EditText font = (EditText) view.findViewById(R.id.font);
					FID = ( font.getText().toString().isEmpty() ? 78 : Integer.parseInt( font.getText().toString() ) );
					
			        //change layout
			        parentController.startPractice();
			        startButton.setOnClickListener(null);
				}
	        });
			
			break;
		}
		
		//get fragment parent object
		parentController = (MainActivity) getActivity();
        
		return view;
	}
	
	
	public int getPID () {
		return PID;
	}
	
	
	public String getFrequencyOfUse () {
		return FREQUENCY_OF_USE;
	}
	
	
	public int getFID () {
		return FID;
	}
}

package gesturelogger.controller;

import gesturelogger.view.SettingsFragment;

import com.example.gesturelogger.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;



/**
 * for Setting within the app
 * @author jalvina
 *
 */
public class SettingActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//this.addPreferencesFromResource(R.xml.preference);
		
		// Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

	}
}

package com.example.testpreferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class DisplaySettingsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_settings_layout);  

	    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    StringBuilder builder = new StringBuilder();
	    
	    // builder.append("\n" + sharedPrefs.getBoolean("perform_updates", false));
	    builder.append("\n User: " + sharedPrefs.getString("auth_username", "-1"));
	    builder.append("\n Pass: " + sharedPrefs.getString("auth_password", "NULL"));
	    builder.append("\n auto_forget: " + sharedPrefs.getBoolean("auto_forget", false));
	    builder.append("\n auto_login: " + sharedPrefs.getBoolean("auto_login", false));
	    
	    TextView settingsTextView = (TextView) findViewById(R.id.settings_text_view);
	    settingsTextView.setText(builder.toString());


	}

}
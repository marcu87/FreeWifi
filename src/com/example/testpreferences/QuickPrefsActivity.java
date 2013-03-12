package com.example.testpreferences;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class QuickPrefsActivity extends Activity {

	private QuickPrefsActivity QuickPrefsActivity;
	private Context context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        
        // check if the user has setted his info
        this.checkIfUserHasSetteduserAndPassword();

		setContentView(R.layout.show_settings_layout);
    }
    
    public void initialize(final Context context) {
    	this.context   = context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, 0, 0, "Settings");
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case 0:
    			startActivity(new Intent(this, ShowSettingsActivity.class));
    			return true;
    	}
    	return false;
    }
    
    public void buttonForgetNetwork(View v) {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext() );
    	Integer freeWifiNetworkID = sharedPrefs.getInt("freeWifiNetworkID", 0);
    	
    	// if there are a network to remove:
    	if (freeWifiNetworkID != 0)
    	{
	    	WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    	wifiMgr.removeNetwork(freeWifiNetworkID);
	    	wifiMgr.saveConfiguration();
    	}

		Toast toast = Toast.makeText(getApplicationContext(), "FreeWifi was deleted from your networks", 10000);
		toast.show();
    }
    
    public void buttonConnect(View v) 
    {   
    	if (this.checkIfUserHasSetteduserAndPassword() == false ) return;
    	
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	
    	WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
    	String nameWifi = wifiInfo.getSSID();
    	Integer freeWifiNetworkID = wifiInfo.getNetworkId();

    	if (mWifi.isConnected() ) {

    		if (nameWifi != null && nameWifi.equals("\"FreeWifi\"") )
    		{
    			Editor pName = PreferenceManager
    		            .getDefaultSharedPreferences(getApplicationContext())
    		            .edit();
    		    pName.putInt("freeWifiNetworkID", freeWifiNetworkID);
    		    pName.commit();

    			new postData().execute();
    		}
    		else
    		{
    			new AlertDialog.Builder(this).setTitle("LOL").setMessage("You are not connected to FreeWifi").setNeutralButton("Close", null).show();
    		}

    	}
    	else 
    	{
    		new AlertDialog.Builder(this).setTitle("LOL").setMessage("Your WIFI is off, please connect.").setNeutralButton("Close", null).show();
    	}
    	
    	
    }
    


    private class postData extends AsyncTask<String, Void, String> 
    {
    	
    	private final ProgressDialog dialog = ProgressDialog.show(QuickPrefsActivity.this, "", 
    			"Connecting. Please wait...", true);

    	@Override
    	protected String doInBackground(String... params) {
    		// perform long running operation operation

    		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext() );
    		
    		String userName = sharedPrefs.getString("auth_username", "");
    		String userPass = sharedPrefs.getString("auth_password", "");
    		
    		String server = "https://wifi.free.fr/Auth";
    		
    		HttpClient httpclient = new DefaultHttpClient();
    		HttpPost httppost = new HttpPost(server);

    		try {
    			// Add your data
    			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
    			nameValuePairs.add(new BasicNameValuePair("login", userName));
    			nameValuePairs.add(new BasicNameValuePair("password", userPass));
    			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

    			try {
    				httpclient.execute(httppost);
    			} catch (UnsupportedEncodingException e) {
    				e.printStackTrace();
    			}


    			// Execute HTTP Post Request
    			// ResponseHandler<String> responseHandler=new BasicResponseHandler();
    			// String responseBody = httpclient.execute(httppost, responseHandler);

    			// if (Boolean.parseBoolean(responseBody)) {
    			//	dialog.cancel();
    			// }


    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			Log.i("HTTP Failed", e.toString());
    		}    		
    		
    		return null;
    	}

    	/* (non-Javadoc)
    	 * @see android.os.AsyncTask#onPreExecute()
    	 */
    	@Override
    	protected void onPreExecute() {
    		// Things to be done before execution of long running operation. For example showing ProgessDialog
    		
    		dialog.setCancelable(false);
    		dialog.show();
    	}
    	
    	
    	/* (non-Javadoc)
    	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
    	 */
    	@Override
    	protected void onPostExecute(String result) {
    		// hide the loading:
    		dialog.dismiss();
    		
    		Toast toast = Toast.makeText(getApplicationContext(), "You are connected :-)", 10000);
    		toast.show();
    	}

    	/* (non-Javadoc)
    	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
    	 */
    	@Override
    	protected void onProgressUpdate(Void... values) {
    		// Things to be done while execution of long running operation is in progress. For example updating ProgessDialog

    	}
    }
    
    public boolean checkIfUserHasSetteduserAndPassword()
    {
        // getting user and password
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext() );
		String userName = sharedPrefs.getString("auth_username", "");
		String userPass = sharedPrefs.getString("auth_password", "");
		
    	// First check if the user has setted the user/password:
		if (TextUtils.isEmpty(userPass) || TextUtils.isEmpty(userName) )
		{
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage("Set your FreeWIFI user and password");
	        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	
	            public void onClick(DialogInterface dialog, int id) {
	
	            	Intent i = new Intent(getBaseContext(), ShowSettingsActivity.class);    
	            	startActivity(i);
	
	            }
	
	        });
	
			builder.show();
			return false;
		}
		
		return true;
    }

}
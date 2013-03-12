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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {
	
	private final String TAG = "WifiReceiver";
	
    public void onReceive(Context context, Intent intent) 
    {
    	
        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
        boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

        NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
        
        if(currentNetworkInfo.isConnected() )
        {
        	Log.v(TAG, "ON > "+ currentNetworkInfo.getReason() + " < Extra info > "+ currentNetworkInfo.getExtraInfo()  + " < STATE > " + currentNetworkInfo.getDetailedState() );
        
        	// getting the id of the freeWifi 
        	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext() );
        	Integer freeWifiNetworkID = sharedPrefs.getInt("freeWifiNetworkID", 0);
    		String userName = sharedPrefs.getString("auth_username", "");
    		String userPass = sharedPrefs.getString("auth_password", "");
        	
        	// the "reasons" of the connection
        	String connectionReason = currentNetworkInfo.getReason();
        	String connectionNetworkName = currentNetworkInfo.getExtraInfo(); 
        	
        	// if it connects to FreeWifi stores the wifiID
        	if (connectionNetworkName != null && connectionNetworkName.equals("\"FreeWifi\"") )
        	{
            	WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            	WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            	Integer newfreeWifiNetworkID = wifiInfo.getNetworkId();
            	
    			Editor pName = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
    		    pName.putInt("freeWifiNetworkID", newfreeWifiNetworkID );
    		    pName.commit();
    		    
    		    // *************************
    		    // and now I'll try to login:
    		    //         		
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


        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			Log.i("HTTP Failed", e.toString());
        		}
        	}
        	
        	// check if there are saved a wifiID
        	// and the network is not FreeWifi 
        	if ( freeWifiNetworkID != 0 && ( (connectionNetworkName != null && !connectionNetworkName.equals("\"FreeWifi\"")) || 
    				(connectionReason != null && connectionReason.equals("dataEnabled")) ) 
    			)
        	{
            	// removing from the know networks
        		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            	wifiMgr.removeNetwork(freeWifiNetworkID);
            	wifiMgr.saveConfiguration();

            	// deleting the freeWifiId
    			Editor pName = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
    		    pName.putInt("freeWifiNetworkID", 0);
    		    pName.commit();
            	
            	Log.v(TAG, "Deleted freewifi ID: "+ freeWifiNetworkID );
        	}

        }
        else
        {
        	Log.v(TAG, "OFF > "+ currentNetworkInfo.getReason() + " < Extra info > "+ currentNetworkInfo.getExtraInfo() + " < STATE > " + currentNetworkInfo.getDetailedState() );
        }

    }
    
}
package com.example.testpreferences;

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
	
	/*
	public void onReceive(Context context, Intent intent) {
	    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
	    String wifiStateText = "No State";

	    switch (wifiState) {
	    case WifiManager.WIFI_STATE_DISABLING:
	        wifiStateText = "WIFI_STATE_DISABLING";
	        break;
	    case WifiManager.WIFI_STATE_DISABLED:
	        wifiStateText = "WIFI_STATE_DISABLED";
	        break;
	    case WifiManager.WIFI_STATE_ENABLING:
	        wifiStateText = "WIFI_STATE_ENABLING";
	        break;
	    case WifiManager.WIFI_STATE_ENABLED:
	        wifiStateText = "WIFI_STATE_ENABLED";
	        break;
	    case WifiManager.WIFI_STATE_UNKNOWN:
	        wifiStateText = "WIFI_STATE_UNKNOWN";
	        break;
	    default:
	        break;
	    }
	    Log.v(TAG, "onReceive Broadcast >>> WiFiState: " + wifiStateText);
	    Log.v(TAG, "onReceive Broadcast >>> Time: " + new Date());
	}
	*/
	
    public void onReceive(Context context, Intent intent) 
    {

    	// getting the id of the freeWifi 
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext() );
    	Integer freeWifiNetworkID = sharedPrefs.getInt("freeWifiNetworkID", 0);
    	
        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
        boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

        NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
        
        if(currentNetworkInfo.isConnected()){
        	Log.v(TAG, "ON > "+ currentNetworkInfo.getReason() + " < Extra info > "+ currentNetworkInfo.getExtraInfo()  + " < STATE > " + currentNetworkInfo.getDetailedState() );
        	
        	String connectionReason = currentNetworkInfo.getReason();
        	String connectionNetworkName = currentNetworkInfo.getExtraInfo(); 
        	
        	if (connectionNetworkName != null && connectionNetworkName.equals("\"FreeWifi\"") )
        	{
            	WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            	WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            	Integer newfreeWifiNetworkID = wifiInfo.getNetworkId();
            	
    			Editor pName = PreferenceManager
    		            .getDefaultSharedPreferences(context.getApplicationContext())
    		            .edit();
    		    pName.putInt("freeWifiNetworkID", newfreeWifiNetworkID );
    		    pName.commit();
        	}
        	
        	if ( freeWifiNetworkID != 0 && ( (connectionNetworkName != null && !connectionNetworkName.equals("\"FreeWifi\"") ) || 
    				(connectionReason != null && connectionReason.equals("dataEnabled") ) ) )
        	{
            	// removing from the knowed networks
        		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            	wifiMgr.removeNetwork(freeWifiNetworkID);
            	wifiMgr.saveConfiguration();

            	// deleting the freeWifiId
    			Editor pName = PreferenceManager
    		            .getDefaultSharedPreferences(context.getApplicationContext())
    		            .edit();
    		    pName.putInt("freeWifiNetworkID", 0);
    		    pName.commit();
            	
            	Log.v(TAG, "Deleted freewifi ID: "+ freeWifiNetworkID );
        	}

        }else{
        	Log.v(TAG, "OFF > "+ currentNetworkInfo.getReason() + " < Extra info > "+ currentNetworkInfo.getExtraInfo() + " < STATE > " + currentNetworkInfo.getDetailedState() );
        }
    }
    
}
package com.example.testpreferences;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {
	
	private final String TAG = "WifiReceiver";
	
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
	    Log.v(TAG, "onReceive Broadcast > WiFiState: " + wifiStateText);
	    Log.v(TAG, "onReceive Broadcast > Time: " + new Date());
	}
}
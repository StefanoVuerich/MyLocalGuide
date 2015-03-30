package com.lqc.mylocalguide.wifimanagement;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lqc.mylocalguide.fragments.AdministrationFragment;

public class MyWifiManager implements Runnable{
	
	private WifiManager wifiManager;
	private Context context;
	public static ArrayList<ScanResult> results;
	private int size;
	private boolean isWifiOn = true;
	private Thread thread;
	
	public MyWifiManager(final Context context) {
		
		this.context = context;
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		if(!wifiManager.isWifiEnabled()) {
			Log.v("jajaja", "wifi is disabled..making it enabled");
            isWifiOn = wifiManager.setWifiEnabled(true);
		}
		
		context.registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent) 
            {
               results = (ArrayList<ScanResult>) wifiManager.getScanResults();
               size = results.size();
               
              AdministrationFragment adminFragment = (AdministrationFragment) ((Activity)context).getFragmentManager().findFragmentByTag("AdministrationFragment");
              adminFragment.printWifiList();
               
               for(ScanResult result : results) {
            	   Log.v("jajaja", "Network: " + result.SSID 
            			   			+ "Capabilities: " + result.capabilities
            			   			+ "Level: " + result.level);
               }
            }


        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); 
	}
	
	public void scanForWifi() {
		if(wifiManager != null) {
			wifiManager.startScan();
		}
	}

	@Override
	public void run() {
		while(isWifiOn) {
			scanForWifi();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void stopWifiScan() {
		if(this.thread != null) {
			isWifiOn = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.wifiManager.setWifiEnabled(false);
		}
	}
	
	public void startWifiScan() {
		if(this.thread == null) {
			thread = new Thread(MyWifiManager.this);
			thread.start();
		}
	}
}

package com.lqc.mylocalguide.broadcasting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class MySettingsReceiver extends BroadcastReceiver{

	private IntentFilter[] intentFiltersArray;
	
	public IntentFilter[] getIntentFiltersArray() {
		return intentFiltersArray;
	}
	
	private  MySettingsReceiver() {
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.MAIN");
		intentFilter.addAction("android.settings.SETTINGS");
		intentFilter.addCategory("android.intent.category.DEFAULT");
		intentFilter.addCategory("android.intent.category.LAUNCHER");
		
	}
	
	private static MySettingsReceiver mInstance;
	
	public static MySettingsReceiver get()
	{
		if(mInstance == null)
			mInstance = new MySettingsReceiver();
		
		return mInstance;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("jajaja", "home button action receive");
	}

}

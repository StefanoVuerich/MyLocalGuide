package com.lqc.mylocalguide.broadcasting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class MyIntentReceiver extends BroadcastReceiver{

	private IntentFilter[] intentFiltersArray;
	
	public IntentFilter[] getIntentFiltersArray() {
		return intentFiltersArray;
	}
	
	/*<action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.HOME" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.LAUNCHER" />*/
	
	private  MyIntentReceiver() {
		
		IntentFilter first = new IntentFilter();
		first.addAction("android.intent.action.MAIN");
		first.addCategory("android.intent.category.HOME");
		
		intentFiltersArray = new IntentFilter[1];
		intentFiltersArray[0] = first;
	}
	
	private static MyIntentReceiver mInstance;
	
	public static MyIntentReceiver get()
	{
		if(mInstance == null)
			mInstance = new MyIntentReceiver();
		
		return mInstance;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("jajaja", "home button action receive");
	}

}

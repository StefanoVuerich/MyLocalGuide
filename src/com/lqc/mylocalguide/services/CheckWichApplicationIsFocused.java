package com.lqc.mylocalguide.services;

import java.util.List;

import com.lqc.mylocalguide.MainActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CheckWichApplicationIsFocused extends Service implements Runnable{

	ActivityManager activityManager;
	Thread thread;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		activityManager = (ActivityManager) getApplicationContext()
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		
		thread = new Thread(this);
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void run() {
		
		while(true) {
			
			Log.v("jajaja","running thread");
			
			List<RunningTaskInfo> talksRunning = activityManager.getRunningTasks(10);
			ComponentName activityOnTop = activityManager.getRunningTasks(1).get(0).topActivity;
			if(activityOnTop != null 
					&& (!(activityOnTop.equals("com.lqc.mylocalguide.MainActivity"))
					|| !(activityOnTop.equals("com.android.launcher3.Launcher")))) {
				Log.v("jajaja","activity on top is not MyLOcalGuide or launcher");
				//Intent intent = new Intent("com.lqc.mylocalguide.MainActivity");
				//startActivity(intent);
				break;
			}
		}
	}
}

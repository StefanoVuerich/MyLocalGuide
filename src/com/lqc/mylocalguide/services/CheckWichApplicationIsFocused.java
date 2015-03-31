package com.lqc.mylocalguide.services;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.lqc.mylocalguide.MainActivity;
import com.lqc.mylocalguide.utilities.CustomApplicationClass;

public class CheckWichApplicationIsFocused extends Service implements Runnable {

	private final String LAUNCHER_PACKAGE = "com.android.launcher3.Launcher";
	private final String MAIN_ACTIVITY_PACKAGE = "com.lqc.mylocalguide.MainActivity";
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

		boolean notAllowedApplicationIntercepted = false;

		while (!notAllowedApplicationIntercepted) {

			ComponentName activityOnTop = activityManager.getRunningTasks(1)
					.get(0).topActivity;
			
			Log.v("jajaja", "service is running");
			
			if(activityOnTop.getClassName().equals("com.android.settings.Settings")) {
				continue;
			}

			Log.v("jajaja", "service is running. Component at top is: "
					+ activityOnTop.getClassName());

			if (activityOnTop != null
					&& !((activityOnTop.getClassName().equals(LAUNCHER_PACKAGE)) || (activityOnTop
							.equals(MAIN_ACTIVITY_PACKAGE)))) {

				Log.v("jajaja", "must stop settings is " + CustomApplicationClass.get().mustStopCheckIfSettingsIsOnTop());
				
				if (CustomApplicationClass.get()
						.mustStopCheckIfSettingsIsOnTop() && activityOnTop.getClassName().equals("com.android.systemui.recent.RecentsActivity")) {
					
					List<RunningTaskInfo> runningTasks = activityManager
							.getRunningTasks(2);
					RunningTaskInfo programActivity = runningTasks.get(1);
					activityManager.moveTaskToFront(programActivity.id,
							ActivityManager.MOVE_TASK_NO_USER_ACTION);
					
					Log.v("jajaja", "moved " + programActivity.getClass().getName() + " to top");
					continue;
					
				} else {

					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);

					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					notAllowedApplicationIntercepted = true;
					break;
				}
			}
		}
		//CustomApplicationClass.get().setMustStopCheckIfSettingsIsOnTop(false);
	}
}

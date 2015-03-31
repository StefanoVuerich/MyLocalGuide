package com.lqc.mylocalguide.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

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

		while (!notAllowedApplicationIntercepted
				&& !CustomApplicationClass.get()
						.mustStopCheckWichApplicationInOnTop()) {

			ComponentName activityOnTop = activityManager.getRunningTasks(1)
					.get(0).topActivity;

			if (activityOnTop != null
					&& !((activityOnTop.getClassName().equals(LAUNCHER_PACKAGE)) || (activityOnTop
							.equals(MAIN_ACTIVITY_PACKAGE)))) {
				
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);

				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				notAllowedApplicationIntercepted = true;
				break;
			}
		}
		CustomApplicationClass.get().setMustStopCheckWichApplicationInOnTop(
				false);
	}
}

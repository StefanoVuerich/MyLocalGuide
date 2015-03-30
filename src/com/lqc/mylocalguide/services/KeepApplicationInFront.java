package com.lqc.mylocalguide.services;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.lqc.mylocalguide.utilities.CustomApplicationClass;

public class KeepApplicationInFront extends Service implements Runnable {

	private ActivityManager activityManager;
	private int startId;
	private Thread thread;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		activityManager = (ActivityManager) getApplicationContext()
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);

		this.startId = startId;
		startmonitoring();

		return super.onStartCommand(intent, flags, startId);
	}

	private void startmonitoring() {

		thread = new Thread(this);
		thread.run();

	}

	@Override
	public void run() {
		boolean isOnTop = false;

		while (!isOnTop ) {
			
			if(CustomApplicationClass.get().isTryingToExitApplication()) {
				CustomApplicationClass.get().setIsTryingToExitApplication(false);
				break;
			}

			ComponentName topActivity = activityManager.getRunningTasks(1)
					.get(0).topActivity;
			
			
			if (topActivity != null
					&& topActivity.getClassName().equals(
							"com.android.settings.Settings")) {
				Log.v("jajaja", "settings discovered");
				
				CustomApplicationClass.get().setHasTriedToAccessSettings(
						true);
				
				if (CustomApplicationClass.get().hasTriedToAccessSettings()) {
					
					if(!CustomApplicationClass.get().hasSendedPasswordToAccessSettings()) {
						focusOnApp();
					}
					else {
						CustomApplicationClass.get().setHasTriedToAccessSettings(false);
						CustomApplicationClass.get().setHasSendedPasswordToAccessSettings(false);
					}
					
					break;
				} else {
					CustomApplicationClass.get().setHasTriedToAccessSettings(
							true);
					focusOnApp();
				}
			}

			else if (topActivity != null
					&& topActivity.getClassName().equals(
							"com.lqc.mylocalguide.MainActivity")) {
				Log.v("jajaja", "MyLocalGuide is at top");
				isOnTop = true;
				boolean isServiceStopped = this.stopSelfResult(this.startId);
				if (isServiceStopped)
					Log.v("jajaja", "service is stopped");
			} else {

				focusOnApp();

			}
		}

		try {
			this.thread.join();
			Log.v("jajaja", "thread joined");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void focusOnApp() {
		List<RunningTaskInfo> runningTasks = activityManager
				.getRunningTasks(2);
		RunningTaskInfo programActivity = runningTasks.get(1);
		activityManager.moveTaskToFront(programActivity.id,
				ActivityManager.MOVE_TASK_NO_USER_ACTION);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}

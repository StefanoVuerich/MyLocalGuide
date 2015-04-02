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

public class SettingsLevelService extends Service {

	private final String LAUNCHER_PACKAGE = "com.android.launcher3.Launcher";
	private final String MAIN_ACTIVITY_PACKAGE = "com.lqc.mylocalguide.MainActivity";
	private ActivityManager activityManager;
	private Thread thread;
	private boolean isStopped;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		this.isStopped = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.v("jajaja", "settings service started");
		
		activityManager = (ActivityManager) getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);

		isStopped = false;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (!isStopped && !CustomApplicationClass.get().isTryingToExitApplication()) {

					ComponentName topActivity = activityManager
							.getRunningTasks(1).get(0).topActivity;
					
					if(topActivity.getClassName().equals("com.android.settings.Settings$WifiSettingsActivity")){
						Log.v("jajaja","wifi settings discovered");
						focusOnSettings();
						continue;
					}
					

					if (topActivity.getClassName().equals(
							"com.android.settings.Settings")) {
						// if is setting do nothing and continue
						//focusOnApp();
						continue;
					}

					if (topActivity != null
							&& (topActivity.getClassName()
									.equals("com.android.systemui.recent.RecentsActivity"))) {

						Log.v("jajaja",
								"activity at top is recent, focus on app");

						// focus or settings
						focusOnApp();
						continue;

					}

					if (topActivity != null
							&& !((topActivity.getClassName()
									.equals(LAUNCHER_PACKAGE)) || (topActivity
									.equals(MAIN_ACTIVITY_PACKAGE)))) {

						// if activity that is on top is mainActivity or Launcer activity break cycle
						focusOnSettings();
						break;
					}
				}
			}
		});
		thread.run();

		return super.onStartCommand(intent, flags, startId);
	}
	
	private void focusOnSettings() {
		List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(5);
		activityManager.moveTaskToFront((findTopStackLevelApp(runningTasks)).id,
				ActivityManager.MOVE_TASK_NO_USER_ACTION);
		
	}
	
	private RunningTaskInfo findTopStackLevelApp(List<RunningTaskInfo> runningTasks) {
		//logica non molto corretta...solo per evitare il crash
		RunningTaskInfo tmp = new RunningTaskInfo();
		tmp.baseActivity = new ComponentName("com.lqc.mylocalguide", "MainActivity");
		for(RunningTaskInfo task : runningTasks) {
			if(task.baseActivity.getClassName().equals("com.android.settings.Settings")) {
				tmp = task;
				break;
			};
		}
		return tmp;
	}

	private void focusOnApp() {
		List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(2);
		RunningTaskInfo programActivity = runningTasks.get(1);
		Log.v("jajaja", "settings service app that is focusing is "
				+ programActivity.baseActivity.getClassName());
		activityManager.moveTaskToFront(programActivity.id,
				ActivityManager.MOVE_TASK_NO_USER_ACTION);
	}
}

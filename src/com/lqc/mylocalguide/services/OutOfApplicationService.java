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

public class OutOfApplicationService extends Service {

	private ActivityManager activityManager;
	private boolean isStopped;
	private Thread thread;
	private final String LAUNCHER_PACKAGE = "com.android.launcher3.Launcher";
	private final String MAIN_ACTIVITY_PACKAGE = "com.lqc.mylocalguide.MainActivity";

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

		Log.v("jajaja", "out of app started");
		activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		isStopped = false;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (!isStopped
						&& CustomApplicationClass.get()
								.isTryingToExitApplication()) {
					
					Log.v("jajaja", "out of app si running");

					ComponentName topActivity = activityManager
							.getRunningTasks(1).get(0).topActivity;

					if (topActivity.getClassName().equals(
							"com.android.settings.Settings")) {
						focusOnApp();
						continue;
					}

					else if (topActivity != null
							&& (topActivity.getClassName()
									.equals("com.android.systemui.recent.RecentsActivity"))) {

						Log.v("jajaja",
								"activity at top is recent, focus on app");

						// focus or settings
						focusOnApp();
						continue;

					}
					
					else if (topActivity != null
							&& !((topActivity.getClassName()
									.equals(LAUNCHER_PACKAGE)) || (topActivity
									.equals(MAIN_ACTIVITY_PACKAGE)))) {

						focusOnApp();
						continue;
					}
				}
			}
		});
		
		thread.start();

		return super.onStartCommand(intent, flags, startId);
	}

	private void focusOnApp() {
		List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(5);
		activityManager.moveTaskToFront((findTopStackLevelApp(runningTasks)).id,
				ActivityManager.MOVE_TASK_NO_USER_ACTION);
	}

	private RunningTaskInfo findTopStackLevelApp(List<RunningTaskInfo> runningTasks) {
		RunningTaskInfo tmp = null;
		for(RunningTaskInfo task : runningTasks) {
			if(task.baseActivity.getClassName().equals(MAIN_ACTIVITY_PACKAGE)
					|| task.baseActivity.getClassName().equals(LAUNCHER_PACKAGE)) {
				tmp = task;
				break;
			};
		}
		return tmp;
	}
}

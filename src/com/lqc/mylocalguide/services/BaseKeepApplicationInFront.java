package com.lqc.mylocalguide.services;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.lqc.mylocalguide.utilities.CustomApplicationClass;

public class BaseKeepApplicationInFront extends Service {

	private ActivityManager activityManager;
	Thread thread;
	private static final String SETTINGS_PACKAGE_NAME = "com.android.settings.Settings";
	private static final String RESOLVER_ACTIVITY_PACKAGE_NAME = "com.android.internal.app.ResolverActivity";
	private static final String MAIN_ACTIVITY_PACKAGE_NAME = "com.lqc.mylocalguide.MainActivity";
	private boolean isStopped;

	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.isStopped = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		activityManager = (ActivityManager) getApplicationContext()
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		
		isStopped = false;

		new Thread(new Runnable() {
			public void run() {
				
				boolean hasFoundBadApplicationOnTop = false;
				
				while(!isStopped && !CustomApplicationClass.get().isTryingToExitApplication()) {
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					ComponentName topActivity = activityManager.getRunningTasks(1)
							.get(0).topActivity;
					
					//Log.v("jajaja", "thread is running. Activity on top is: " + topActivity.getClassName());
					
					//check if activity on top is main activity
					if (topActivity != null
							&& (topActivity.getClassName().equals(
									MAIN_ACTIVITY_PACKAGE_NAME))) {
						
						continue;
					}
					
					//check if activity on top is settings
					if (topActivity != null
							&& /*(topActivity.getClassName().equals(
									RESOLVER_ACTIVITY_PACKAGE_NAME)
									||*/
									(topActivity.getClassName().equals(
											SETTINGS_PACKAGE_NAME)
									))/*)*/ {
						
						
						
						CustomApplicationClass.get().setHasTriedToAccessSettings(
								true);
						
						//Log.v("jajaja", "on top is settings");

						//check if has user has sended right password to show settings
						if(CustomApplicationClass.get().mustStopCheckIfSettingsIsOnTop() 
								&& !CustomApplicationClass.get().isLoginSettingsoccupied) 
						{
							//Log.v("jajaja","inside if");
							//dont want to show settings password dialog anymore
							CustomApplicationClass.get().setHasTriedToAccessSettings(false);
						}
						else {
							focusOnApp();
						}
						
						//Log.v("jajaja","dopo else id continue");
						hasFoundBadApplicationOnTop =true;
						continue;
									
					}
					//check if activity on top is toogle recents
					if(topActivity != null
							&& (topActivity.getClassName().equals(
									"com.android.systemui.recent.RecentsActivity"))){
						
						//Log.v("jajaja", "recents activity discovered");
						//toggleRecents();
						focusOnApp();
						continue;
						
					}
					//check if activity on top is something else
					else if (topActivity != null
							&& (!(topActivity.getClassName().equals(
									RESOLVER_ACTIVITY_PACKAGE_NAME)
									||
									!(topActivity.getClassName().equals(
											SETTINGS_PACKAGE_NAME)
											||
											(!(topActivity.getClassName().equals(
													MAIN_ACTIVITY_PACKAGE_NAME)
									)))))) {
						focusOnApp();
						hasFoundBadApplicationOnTop = true;
					}
				}
				
				
			}
		}).start();

		return super.onStartCommand(intent, flags, startId);
	}

	private void focusOnApp() {
		
		// check if activity at 1 is launcher 3 / if yes intent myactivity
		
		List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(2);
		RunningTaskInfo programActivity = runningTasks.get(1);
		activityManager.moveTaskToFront(programActivity.id,
				ActivityManager.MOVE_TASK_NO_USER_ACTION);
		
		//Log.v("jajaja","moving task " + programActivity.baseActivity.getClassName() + " to front");
	}
	
	private void toggleRecents() {
		List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(10);
		
	    /*Intent closeRecents = new Intent("com.android.systemui.recent.action.TOGGLE_RECENTS");
	    closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    ComponentName recents = new ComponentName("com.android.systemui", "com.android.systemui.recent.RecentsActivity");
	    closeRecents.setComponent(recents);
	    this.startActivity(closeRecents);*/
	    
	    
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

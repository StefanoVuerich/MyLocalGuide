package com.lqc.mylocalguide;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lqc.mylocalguide.broadcasting.AutoStart;
import com.lqc.mylocalguide.fragments.AdministrationFragment;
import com.lqc.mylocalguide.fragments.AdministrationFragment.OnActionSelected;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog.ICheckPassword;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment.IExitApplicationConfirm;
import com.lqc.mylocalguide.fragments.MyWebViewFragment;
import com.lqc.mylocalguide.fragments.NoConnectionFragment;
import com.lqc.mylocalguide.login.LoginHandler;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class MainActivity extends Activity implements ICheckPassword,
		OnActionSelected, IExitApplicationConfirm, OnTouchListener {

	@Override
	protected void onResume() {
		super.onResume();
		Log.v("jajaja", "on resume");

		// showNoConnectionFragment();

		task = new MyTask();
		task.execute();

		firstLoop = true;
		Log.v("jajaja",
				"start task with boolean is cancelled = " + task.isCancelled()
						+ task.getStatus().toString());
		hideVirtualButtons();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v("jajaja", "on pause");

		if (task != null) {
			boolean stopped = task.cancel(false);
			Log.v("jajaja", "stopping task");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("jajaja", "on destroy");
	}

	@SuppressLint("InlinedApi")
	private void hideVirtualButtons() {
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		Log.v("jajaja", "Focus changed !");

		if (!hasFocus) {
			Log.v("jajaja", "Lost focus !");

			windowCloseHandler.post(windowCloserRunnable);
		}
	}

	private void toggleRecents() {
		Intent closeRecents = new Intent(
				"com.android.systemui.recent.action.TOGGLE_RECENTS");
		closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		ComponentName recents = new ComponentName("com.android.systemui",
				"com.android.systemui.recent.RecentsActivity");
		closeRecents.setComponent(recents);
		this.startActivity(closeRecents);
	}

	private Handler windowCloseHandler = new Handler();
	private Runnable windowCloserRunnable = new Runnable() {

		@Override
		public void run() {

			ActivityManager am = (ActivityManager) getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);

			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

			if (cn != null
					&& cn.getClassName().equals(
							"com.android.systemui.recent.RecentsActivity")) {
				toggleRecents();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v("jajaja", "on create");

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		initConfigurationStorage();

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		setContentView(R.layout.activity_main);

	}

	TextView noConnectionFeedback;
	static MyTask task;

	boolean firstLoop;
	boolean hasConnection;

	public class MyTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			hasConnection = checkForInternetConnection(MainActivity.this);

			if (hasConnection)
				showWebViewFragment();

			else {

				while (!isCancelled() && !hasConnection) {

					Log.v("jajaja", "checking for connection");

 
					hasConnection = checkForInternetConnection(MainActivity.this);
					if (hasConnection) {
						showWebViewFragment();
						break;
					} else {
						// if (firstLoop) {
						showNoConnectionFragment();
						noConnectionFeedback = (TextView) findViewById(R.id.noInternetTextView);
						Log.v("jajaja", "show no connection fragment");
						// }
						int seconds = 5;
						while (seconds >= 0 && !isCancelled()) {
							Log.v("jajaja", "seconds: " + seconds);
							publishProgress(seconds);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							--seconds;
						}
						firstLoop = false;
					}

				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

			noConnectionFeedback = (TextView) findViewById(R.id.noInternetTextView);

			int count = values[0];

			if (count == 0) {
				noConnectionFeedback
						.setText("Try to established internet connection");
			} else {
				noConnectionFeedback
						.setText("No Internet Connection, retry in " + count);
			}
		}

	}

	private void showNoConnectionFragment() {

		/*
		 * Fragment fragment = getFragmentManager().findFragmentByTag(
		 * NoConnectionFragment.class.getSimpleName());
		 * 
		 * if (fragment != null) { getFragmentManager() .beginTransaction()
		 * .replace(R.id.fragmentsContainer, NoConnectionFragment.getInstance(),
		 * NoConnectionFragment.class.getSimpleName()) .commit(); } else {
		 */

		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentsContainer,
						NoConnectionFragment.getInstance(),
						NoConnectionFragment.class.getSimpleName()).commit();
		// }
	}

	private void initConfigurationStorage() {
		String filename = ConfigurationStorage.STORAGE_FILE_NAME;
		String filePath = Environment.getDataDirectory().getAbsolutePath()
				+ "/data/" + getPackageName() + "/shared_prefs/" + filename;
		File f = new File(filePath);

		if (!f.exists()) {
			ConfigurationStorage.getInstance().init(this);
		}
	}

	boolean first = true;

	private boolean checkForInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(MainActivity.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		if (!isConnected)
			return false;

		return true;
	}

	public void reCheckForConnection() {
		checkForInternetConnection(MainActivity.this);
	}

	@Override
	public void onLogin(String mode_flag, String password) {

		boolean isLoginCorrect = LoginHandler.getInstance().checkLogin(this,
				mode_flag, password);

		if (mode_flag.equals("exit")) {

			if (isLoginCorrect) {

				closeCheckPasswordDialog();
				showConfirmExitDialog();
			} else {
				wrongPassword();
			}

		} else if (mode_flag.equals("admin")) {

			if (isLoginCorrect) {

				closeCheckPasswordDialog();
				showAdministrationFragment();
			} else {
				wrongPassword();
			}
		}
	}

	private void showConfirmExitDialog() {
		ConfirmApplicationExitFragment confirmExitDialog = ConfirmApplicationExitFragment
				.get();
		confirmExitDialog.show(getFragmentManager(),
				ConfirmApplicationExitFragment.getTAG());
	}

	private void wrongPassword() {
		CheckPasswordDialog checkPasswordFragment = (CheckPasswordDialog) getFragmentManager()
				.findFragmentByTag(CheckPasswordDialog.TAG);
		checkPasswordFragment.clearPasswordEditText();
		checkPasswordFragment.shake();
	}

	@Override
	public void onCancel() {
		closeCheckPasswordDialog();

	}

	private void closeCheckPasswordDialog() {
		CheckPasswordDialog checkPasswordDialog = (CheckPasswordDialog) getFragmentManager()
				.findFragmentByTag(CheckPasswordDialog.TAG);
		checkPasswordDialog.dismiss();
	}

	@Override
	public void onSave() {
		showWebViewFragment();
	}

	@Override
	public void onCancelSave() {
		showWebViewFragment();
	}

	private void showWebViewFragment() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentsContainer, MyWebViewFragment.get(),
						MyWebViewFragment._TAG).commit();
	}

	private void showAdministrationFragment() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentsContainer,
						AdministrationFragment.getInstance(),
						AdministrationFragment._TAG).addToBackStack(null)
				.commit();
	}

	@Override
	public void onExitApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		closeConfirmExitApplicationFragment();
		showWebViewFragment();
	}

	@Override
	public void onCancelExit() {
		closeConfirmExitApplicationFragment();
	}

	private void closeConfirmExitApplicationFragment() {
		ConfirmApplicationExitFragment confirmApplicationExitFragment = (ConfirmApplicationExitFragment) getFragmentManager()
				.findFragmentByTag(ConfirmApplicationExitFragment.getTAG());
		confirmApplicationExitFragment.dismiss();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Toast.makeText(MainActivity.this, "touch", Toast.LENGTH_LONG).show();
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v("jajaja", "on stop receiver unregistered");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.v("jajaja", "on start");

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.v("jajaja", "on restart");
	}
}

package com.lqc.mylocalguide;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lqc.mylocalguide.fragments.AdministrationFragment;
import com.lqc.mylocalguide.fragments.AdministrationFragment.OnActionSelected;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog.ICheckPassword;
import com.lqc.mylocalguide.fragments.CheckSettingsPasswordDialog;
import com.lqc.mylocalguide.fragments.CheckSettingsPasswordDialog.ICheckSettingsPassword;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment.IExitApplicationConfirm;
import com.lqc.mylocalguide.fragments.MyWebViewFragment;
import com.lqc.mylocalguide.fragments.NoConnectionFragment;
import com.lqc.mylocalguide.login.LoginHandler;
import com.lqc.mylocalguide.services.CheckWichApplicationIsFocused;
import com.lqc.mylocalguide.services.KeepApplicationInFront;
import com.lqc.mylocalguide.storage.ConfigurationStorage;
import com.lqc.mylocalguide.utilities.CustomApplicationClass;

public class MainActivity extends Activity implements ICheckPassword,
		OnActionSelected, IExitApplicationConfirm, OnTouchListener,
		ICheckSettingsPassword {

	boolean first = true;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v("jajaja", "on create");

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

	@Override
	protected void onPause() {
		super.onPause();
		Log.v("jajaja", "on pause");
		Intent intent = new Intent(this, KeepApplicationInFront.class);
		startService(intent);

		if (task != null) {
			task.cancel(false);
			Log.v("jajaja", "stopping task");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v("jajaja", "on resume");

		task = new MyTask();
		task.execute();

		firstLoop = true;

		hideVirtualButtons();

		Log.v("jajaja", "boolean is: "
				+ CustomApplicationClass.get().hasTriedToAccessSettings());
		if (CustomApplicationClass.get().hasTriedToAccessSettings()) {
			showSettingsPasswordDialog();
		}
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("jajaja", "on destroy");
	}

	TextView noConnectionFeedback;
	public MyTask task;
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
						// Log.v("jajaja", "show no connection fragment");
						// }
						int seconds = 5;
						while (seconds >= 0 && !isCancelled()) {
							// Log.v("jajaja", "seconds: " + seconds);
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
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentsContainer,
						NoConnectionFragment.getInstance(),
						NoConnectionFragment.class.getSimpleName()).commit();
	}

	private void initConfigurationStorage() {
		String filename = ConfigurationStorage.STORAGE_FILE_NAME;
		String filePath = Environment.getDataDirectory().getAbsolutePath()
				+ "/data/" + getPackageName() + "/shared_prefs/" + filename;
		File file = new File(filePath);

		if (!file.exists()) {
			ConfigurationStorage.getInstance().init(this);
		}
	}

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

	public void showConfirmExitDialog() {
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
	
	private void wrongSettingsPassword() {
		CheckSettingsPasswordDialog checkSettingsPasswordFragment = (CheckSettingsPasswordDialog) getFragmentManager()
				.findFragmentByTag(CheckSettingsPasswordDialog.TAG);
		checkSettingsPasswordFragment.clearPasswordEditText();
		checkSettingsPasswordFragment.shake();
	}

	@Override
	public void onCancel() {
		closeCheckPasswordDialog();

	}

	private void showSettingsPasswordDialog() {
		CheckSettingsPasswordDialog checkSettingsPasswordDialog = CheckSettingsPasswordDialog
				.get("admin");
		checkSettingsPasswordDialog.show(getFragmentManager(),
				CheckPasswordDialog.TAG);
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
		CustomApplicationClass.get().setIsTryingToExitApplication(true);
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
		Log.v("jajaja", "on stop");
		Intent intent = new Intent(this, CheckWichApplicationIsFocused.class);
		startService(intent);

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

	@Override
	public void onSettingsCheckPassword(String password) {
		boolean isLoginCorrect = LoginHandler.getInstance().checkLogin(this,
				"admin", password);

		CustomApplicationClass.get().setHasSendedPasswordToAccessSettings(true);
		
		if (isLoginCorrect) {
			onSettingsCancel();
			Intent intent = new Intent(Settings.ACTION_SETTINGS);
			startActivity(intent);
		} else {
			CustomApplicationClass.get().setHasSendedPasswordToAccessSettings(false);
			wrongSettingsPassword();
		}

		CustomApplicationClass.get().setHasTriedToAccessSettings(false);
	}

	@Override
	public void onSettingsCancel() {
		CheckSettingsPasswordDialog settingsPasswordDialog = (CheckSettingsPasswordDialog) getFragmentManager()
				.findFragmentByTag(CheckSettingsPasswordDialog.TAG);
		settingsPasswordDialog.dismiss();
		CustomApplicationClass.get().setHasTriedToAccessSettings(false);
	}
}

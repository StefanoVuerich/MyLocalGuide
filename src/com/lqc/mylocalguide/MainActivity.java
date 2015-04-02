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
import android.view.View;
import android.widget.TextView;

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
import com.lqc.mylocalguide.login.LoginModes;
import com.lqc.mylocalguide.services.BaseKeepApplicationInFront;
import com.lqc.mylocalguide.services.OutOfApplicationService;
import com.lqc.mylocalguide.services.SettingsLevelService;
import com.lqc.mylocalguide.storage.ConfigurationStorage;
import com.lqc.mylocalguide.utilities.CustomApplicationClass;

public class MainActivity extends Activity implements ICheckPassword,
		OnActionSelected, IExitApplicationConfirm ,
		ICheckSettingsPassword {

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

		if (task != null) {
			task.cancel(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (CustomApplicationClass.get().isTryingToExitApplication())
			CustomApplicationClass.get().setIsTryingToExitApplication(false);

		task = new MyTask();
		task.execute();

		firstLoop = true;

		hideVirtualButtons();

		if (CustomApplicationClass.get().mustStopCheckIfSettingsIsOnTop()) {
			CustomApplicationClass.get().setMustStopCheckIfSettingsIsOnTop(
					false);
		}
		
		if (CustomApplicationClass.get().hasTriedToAccessSettings()) {
			if(getFragmentManager().findFragmentByTag(CheckSettingsPasswordDialog._TAG) == null)
				showSettingsPasswordDialog();
		}
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

					hasConnection = checkForInternetConnection(MainActivity.this);
					if (hasConnection) 
					{
						showWebViewFragment();
						break;
					} 
					else 
					{
						showNoConnectionFragment();
						noConnectionFeedback = (TextView) findViewById(R.id.noInternetTextView);
						int seconds = 5;
						while (seconds >= 0 && !isCancelled()) {
							publishProgress(seconds);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
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

		if (mode_flag.equalsIgnoreCase(LoginModes.EXIT.name())) {

			if (isLoginCorrect) {

				closeCheckPasswordDialog();
				showConfirmExitDialog();
			} else {
				wrongPassword();
			}

		} else if (mode_flag.equalsIgnoreCase(LoginModes.ADMIN.name())) {

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
				.findFragmentByTag(CheckSettingsPasswordDialog._TAG);
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
				CheckSettingsPasswordDialog._TAG);
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
		// stop infinite check loop
		CustomApplicationClass.get().setIsTryingToExitApplication(true);
		Intent stopServiceIntent = new Intent(this,
				BaseKeepApplicationInFront.class);
		if (stopService(stopServiceIntent))
			Log.v("jajaja", "service have been stopped");

		Intent showDesktopIntent = new Intent();
		showDesktopIntent.setAction("android.intent.action.MAIN");
		showDesktopIntent.addCategory("android.intent.category.MONKEY");
		startActivity(showDesktopIntent);

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
	protected void onStart() {
		super.onStart();
		
		Intent secondLevelService = new Intent(this, SettingsLevelService.class);
		if (stopService(secondLevelService))
			Log.v("jajaja", "settings level service destroyed");
		
		Intent outOfAppLevelService = new Intent(this, OutOfApplicationService.class);
		if (stopService(outOfAppLevelService))
			Log.v("jajaja", " outOfAppLevelService service destroyed");

		if (CustomApplicationClass.get().isTryingToExitApplication())
			CustomApplicationClass.get().setIsTryingToExitApplication(false);

		Intent baseServiceIntent = new Intent(this,
				BaseKeepApplicationInFront.class);
		startService(baseServiceIntent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		Intent baseServiceIntent = new Intent(this,
				BaseKeepApplicationInFront.class);
		if (stopService(baseServiceIntent))
			Log.v("jajaja", "base service destroyed");

		Intent secondLevelService = new Intent(this, SettingsLevelService.class);
		startService(secondLevelService);

		Intent outOfAppLevelService = new Intent(this,
				OutOfApplicationService.class);
		startService(outOfAppLevelService);
	}

	@Override
	public void onSettingsCheckPassword(String password) {
		boolean isLoginCorrect = LoginHandler.getInstance().checkLogin(this,
				LoginModes.EXIT.name(), password);

		CustomApplicationClass.get().setHasSendedPasswordToAccessSettings(true);

		if (isLoginCorrect) {
			onSettingsCancel();
			CustomApplicationClass.get()
					.setMustStopCheckIfSettingsIsOnTop(true);
			CustomApplicationClass.get().isLoginSettingsoccupied = false;
			Intent intent = new Intent(Settings.ACTION_SETTINGS);
			startActivity(intent);
		} else {
			CustomApplicationClass.get().setHasSendedPasswordToAccessSettings(
					false);
			wrongSettingsPassword();
		}
		CustomApplicationClass.get().setHasTriedToAccessSettings(false);
	}

	@Override
	public void onSettingsCancel() {
		CheckSettingsPasswordDialog settingsPasswordDialog = (CheckSettingsPasswordDialog) getFragmentManager()
				.findFragmentByTag(CheckSettingsPasswordDialog._TAG);
		settingsPasswordDialog.dismiss();
		CustomApplicationClass.get().setHasTriedToAccessSettings(false);
	}
}

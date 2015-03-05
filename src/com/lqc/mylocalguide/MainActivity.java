package com.lqc.mylocalguide;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

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
		OnActionSelected, IExitApplicationConfirm {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE);

		setContentView(R.layout.activity_main);
		initConfigurationStorage();

		checkForInternetConnection(MainActivity.this);	
	}

	private void showNoConnectionFragment() {
		getFragmentManager().beginTransaction().add(R.id.fragmentsContainer, NoConnectionFragment.getInstance(), null).commit();
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

	private void checkForInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(MainActivity.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		if(!isConnected)
			showNoConnectionFragment();
		else
			showWebViewFragment();
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
				.replace(R.id.fragmentsContainer, AdministrationFragment.getInstance(),
						AdministrationFragment._TAG).addToBackStack(null)
				.commit();	
	}

	@Override
	public void onExitApplication() {
		System.exit(0);
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
}

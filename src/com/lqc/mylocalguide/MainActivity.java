package com.lqc.mylocalguide;

import java.io.File;

import com.lqc.mylocalguide.fragments.AdministrationFragment;
import com.lqc.mylocalguide.fragments.AdministrationFragment.OnActionSelected;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment.IExitApplicationConfirm;
import com.lqc.mylocalguide.fragments.WebViewFragment;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog.ICheckPassword;
import com.lqc.mylocalguide.login.LoginHandler;
import com.lqc.mylocalguide.login.LoginModes;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements ICheckPassword,
		OnActionSelected, IExitApplicationConfirm {

	public static final String WEB_VIEW_FRAGMENT = "WEB_VIEW_FRAGMENT";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File f = new File(
				"/data/data/com.lqc.mylocalguide/shared_prefs/my_local_guide_storage.xml");
		if (!f.exists()) {
			ConfigurationStorage.getInstance().init(this);
		}		

		setContentView(R.layout.activity_main);

		showWebViewFragment();
	}

	@Override
	public void onLogin(String mode_flag, String password) {

		boolean isLoginCorrect = LoginHandler.getInstance().checkLogin(this,
				mode_flag, password);

		if (mode_flag.equals("exit")) {

			if (isLoginCorrect) {

				closeCheckPasswordDialog();
				onExitApplication();
			} else {
				wrongPassword();
			}

		} else if (mode_flag.equals("admin")) {

			if (isLoginCorrect) {

				closeCheckPasswordDialog();
				Fragment fr = new AdministrationFragment();
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragmentsContainer, fr);
				ft.commit();
			} else {
				wrongPassword();
			}
		}
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
	public void OnSave() {
		showWebViewFragment();
	}

	@Override
	public void OnCancel() {
		showWebViewFragment();
	}

	public void showWebViewFragment() {
		Fragment fragment = WebViewFragment.get();

		getFragmentManager().beginTransaction()
				.add(R.id.fragmentsContainer, fragment, WEB_VIEW_FRAGMENT)
				.commit();
	}

	@Override
	public void onExitApplication() {
		System.exit(0);
	}

	@Override
	public void onCancelExit() {
		// Do nothing
	}
}

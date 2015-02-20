package com.lqc.mylocalguide;

import java.io.File;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;

import com.lqc.mylocalguide.fragments.AdministrationFragment;
import com.lqc.mylocalguide.fragments.AdministrationFragment.OnActionSelected;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog.ICheckPassword;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment.IExitApplicationConfirm;
import com.lqc.mylocalguide.fragments.WebViewFragment;
import com.lqc.mylocalguide.login.LoginHandler;
import com.lqc.mylocalguide.storage.ConfigurationStorage;
import com.lqc.mylocalguide.utilities.FragmentsFlags;

public class MainActivity extends Activity implements ICheckPassword,
		OnActionSelected, IExitApplicationConfirm {

	private static final String CURRENT_ACTIVE_FRAGMENT_TAG = "CURRENT_ACTIVE_FRAGMENT_TAG";
	private FragmentsFlags activeFragment = null;
	private static final boolean EXIST = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String filename = ConfigurationStorage.STORAGE_FILE_NAME;
		String filePath = Environment.getDataDirectory().getAbsolutePath()
				+ "/data/" + getPackageName() + "/shared_prefs/" + filename;
		File f = new File(filePath);

		if (!f.exists()) {
			ConfigurationStorage.getInstance().init(this);
		}

		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			int activeFragment = savedInstanceState
					.getInt(CURRENT_ACTIVE_FRAGMENT_TAG);
			if (activeFragment == FragmentsFlags.WEBVIEWFRAGMENT.getPosition())
				showWebViewFragment(EXIST);
			else if (activeFragment == FragmentsFlags.ADMINISTRATIONFRAGMENT
					.getPosition())
				showAdministrationFragment(EXIST);
		} else {
			if (activeFragment == null
					|| activeFragment == FragmentsFlags.WEBVIEWFRAGMENT)
				showWebViewFragment(!EXIST);
			else if (activeFragment != null
					&& activeFragment == FragmentsFlags.ADMINISTRATIONFRAGMENT)
				showAdministrationFragment(!EXIST);
		}
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
				showAdministrationFragment(!EXIST);
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// fragmentFlag

		Bundle vBundle = new Bundle();
		vBundle.putInt(CURRENT_ACTIVE_FRAGMENT_TAG,
				activeFragment.getPosition());
		outState.putAll(vBundle);
	}

	@Override
	public void onSave() {
		showWebViewFragment(!EXIST);
	}

	@Override
	public void onCancelSave() {
		showWebViewFragment(!EXIST);
	}

	private void showWebViewFragment(boolean exist) {
		Fragment fragment;
		if (exist) {
			fragment = getFragmentManager().findFragmentByTag(
					WebViewFragment._TAG);
		} else
			fragment = WebViewFragment.get();

		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentsContainer, fragment,
						WebViewFragment._TAG).commit();

		activeFragment = FragmentsFlags.WEBVIEWFRAGMENT;
	}

	private void showAdministrationFragment(boolean exist) {
		Fragment fragment;
		if (exist) {
			fragment = getFragmentManager().findFragmentByTag(
					AdministrationFragment.ADMINISTRATION_FRAGMENT_FLAG);
		} else
			fragment = AdministrationFragment.getInstance();

		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentsContainer, fragment,
						AdministrationFragment.ADMINISTRATION_FRAGMENT_FLAG)
				.commit();

		activeFragment = FragmentsFlags.ADMINISTRATIONFRAGMENT;
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

package com.lqc.mylocalguide;

import java.io.File;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import com.lqc.mylocalguide.feedbacks.ErrorHandlerNoIUFragment.IErrorHandler;
import com.lqc.mylocalguide.fragments.AdministrationFragment;
import com.lqc.mylocalguide.fragments.AdministrationFragment.OnActionSelected;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog.ICheckPassword;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment.IExitApplicationConfirm;
import com.lqc.mylocalguide.fragments.WebViewFragment;
import com.lqc.mylocalguide.login.LoginHandler;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class MainActivity extends Activity implements ICheckPassword,
		OnActionSelected, IExitApplicationConfirm, IErrorHandler {

	public static final String WEB_VIEW_FRAGMENT = "WEB_VIEW_FRAGMENT";

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

		showWebViewFragment();
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
		closeConfirmExitApplicationFragment();
	}

	private void closeConfirmExitApplicationFragment() {
		ConfirmApplicationExitFragment confirmApplicationExitFragment = (ConfirmApplicationExitFragment) getFragmentManager()
				.findFragmentByTag(ConfirmApplicationExitFragment.getTAG());
		confirmApplicationExitFragment.dismiss();
	}

	@Override
	public void onPasswordDoesNotMatchError(int who) {
		int textViewReference = 0;
		switch (who) {
		case 0:
			textViewReference = R.id.newAdminPasswordFeedback;
			break;
		case 1:
			textViewReference = R.id.newUserPasswordFeedback;
			break;
		}

		TextView feedbackTxt = (TextView) findViewById(textViewReference);
		feedbackTxt.setText("");
		feedbackTxt.setText("Password and confirm password must be the same");
	}
}

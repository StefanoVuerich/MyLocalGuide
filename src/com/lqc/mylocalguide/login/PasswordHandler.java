package com.lqc.mylocalguide.login;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

import com.lqc.mylocalguide.feedbacks.ErrorHandlerNoIUFragment;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class PasswordHandler {

	private static PasswordHandler instance;

	private PasswordHandler() {
	}

	public static PasswordHandler getInstance() {
		if (instance == null) {
			instance = new PasswordHandler();
		}
		return instance;
	}

	public boolean changeAdminPassword(Fragment fragment, Context context,
			String password, String confirmPassword) {
		if (arePasswordEquals(password, confirmPassword)) {
			ConfigurationStorage.getInstance().updateAdminPassword(context,
					password);
			return true;
		} else {
			triggerPasswordNotMatchError(0, fragment);
			return false;
		}
	}

	public boolean changeUserPassword(Fragment fragment, Context context,
			String password, String confirmPassword) {
		if (arePasswordEquals(password, confirmPassword)) {
			ConfigurationStorage.getInstance().updateUserPassword(context,
					password);
			return true;
		} else {
			triggerPasswordNotMatchError(1, fragment);
			return false;
		}
	}

	// 0 is admin, 1 is user
	private void triggerPasswordNotMatchError(int who, Fragment fragment) {
		ErrorHandlerNoIUFragment errorFragment = ErrorHandlerNoIUFragment
				.getInstance(who);
		fragment.getFragmentManager().beginTransaction()
				.add(errorFragment, ErrorHandlerNoIUFragment.TAG).commit();
		Fragment fr = fragment.getFragmentManager().findFragmentByTag(ErrorHandlerNoIUFragment.TAG);
	}

	private boolean arePasswordEquals(String password, String conformPassword) {
		if (password.equals(conformPassword))
			return true;
		return false;
	}
}

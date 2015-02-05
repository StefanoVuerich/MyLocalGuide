package com.lqc.mylocalguide.login;

import android.content.Context;

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

	public void changeAdminPassword(Context context, String password,
			String confirmPassword) {
		if (arePasswordEquals(password, confirmPassword))
			ConfigurationStorage.getInstance().updateAdminPassword(context, password);
	}

	public void changeUserPassword(Context context, String password,
			String confirmPassword) {
		if (arePasswordEquals(password, confirmPassword))
			ConfigurationStorage.getInstance().updateUserPassword(context, password);
	}

	private boolean arePasswordEquals(String password, String conformPassword) {
		if (password.equals(conformPassword))
			return true;
		return false;
	}
}

package com.lqc.mylocalguide.login;

import android.content.Context;
import android.content.SharedPreferences;

import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class LoginHandler {

	private static LoginHandler instance;

	private LoginHandler() {
	}

	public static LoginHandler getInstance() {
		if (instance == null) {
			instance = new LoginHandler();
		}
		return instance;
	}

	public boolean checkLogin(Context context, String modeFlag, String password) {

		SharedPreferences settings = context.getSharedPreferences(ConfigurationStorage.getInstance().STORAGE, 0);
		
		// check if mode is exit
		if (modeFlag.equalsIgnoreCase(LoginModes.EXIT.name())) {

			// check for admin or user password
			if (password != null
					&& (password.equals(settings.getString(ConfigurationStorage.ADMIN_PASSWORD, "")) || password
							.equals(settings.getString(ConfigurationStorage.USER_PASSWORD, ""))))
				// right password to exit
				return true;

			else
				// wrong password do nothing
				return false;

		}
		// check if mode is login
		else if (modeFlag.equalsIgnoreCase(LoginModes.ADMIN.name())) {

			// check for admin password
			if (password != null && password.equals(settings.getString(ConfigurationStorage.ADMIN_PASSWORD, ""))) {
				// right password to administrate
				return true;
			} else {
				// wrong password to administrate
				return false;
			}
		}
		return false;
	}
}

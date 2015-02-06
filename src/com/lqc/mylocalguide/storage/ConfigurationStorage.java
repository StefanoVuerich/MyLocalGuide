package com.lqc.mylocalguide.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigurationStorage {

	public final String STORAGE = "my_local_guide_storage";
	public static final String ADMIN_PASSWORD = "Admin_Password";
	public static final String USER_PASSWORD = "User_Password";
	public static final String URL = "Url";
	public static final String ZOOM = "Zoom";
	public static final String STORAGE_FILE_NAME = "my_local_guide_storage.xml";
	private static ConfigurationStorage instance;

	private ConfigurationStorage() {
	}

	public static ConfigurationStorage getInstance() {
		if (instance == null) {
			instance = new ConfigurationStorage();
		}
		return instance;
	}

	public void updateAdminPassword(Context context, String password) {
		SharedPreferences settings = context.getSharedPreferences(STORAGE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ADMIN_PASSWORD, password);
		editor.commit();
	}
	
	public void updateUserPassword(Context context, String password) {
		SharedPreferences settings = context.getSharedPreferences(STORAGE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(USER_PASSWORD, password);
		editor.commit();
	}
	
	public void updateUrl(Context context, String url) {
		SharedPreferences settings = context.getSharedPreferences(STORAGE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(URL, url);
		editor.commit();
	}
	
	public void updateZoom(Context context, int zoom) {
		SharedPreferences settings = context.getSharedPreferences(STORAGE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(ZOOM, zoom);
		editor.commit();
	}
	
	public void init(Context context) {
		SharedPreferences settings = context.getSharedPreferences(STORAGE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ADMIN_PASSWORD, "admin");
		editor.putString(USER_PASSWORD, "user");
		editor.putString(URL, "http://lq-creative.com/");
		editor.putInt(ZOOM, 100);
		editor.commit();
	}
}

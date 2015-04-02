package com.lqc.mylocalguide.utilities;

import android.app.Application;
import android.util.Log;

public class CustomApplicationClass extends Application {
	
	public boolean isLoginSettingsoccupied;
	public boolean isProcessingSettingsPress;
	private boolean mustStopCheckIfSettingsInOnTop;
	private boolean isTryingToExitApplication;
	private boolean flagHasTriedToAccessSettings;
	private boolean flagHasSendedPasswordToAccessSettings;
	private static CustomApplicationClass mInstance;
	
	public static CustomApplicationClass get() 
	{	
		if(mInstance == null)
			mInstance = new CustomApplicationClass();
			
		return mInstance;
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
		Log.v("jajaja", "on create application class");
		
		mInstance = this;
		isTryingToExitApplication = false;
		flagHasTriedToAccessSettings = false;
		flagHasSendedPasswordToAccessSettings = false;
		isProcessingSettingsPress = false;
		
	}
	
	public boolean mustStopCheckIfSettingsIsOnTop() {
		return mustStopCheckIfSettingsInOnTop;
	}

	public void setMustStopCheckIfSettingsIsOnTop(
			boolean mustStopCheckWichApplicationInOnTop) {
		this.mustStopCheckIfSettingsInOnTop = mustStopCheckWichApplicationInOnTop;
	}

	public boolean isTryingToExitApplication() {
		return isTryingToExitApplication;
	}

	public void setIsTryingToExitApplication(boolean isTryingToExitApplication) {
		this.isTryingToExitApplication = isTryingToExitApplication;
	}
	
	public boolean hasSendedPasswordToAccessSettings() {
		return flagHasSendedPasswordToAccessSettings;
	}

	public void setHasSendedPasswordToAccessSettings(
			boolean flagHasSendedPasswordToAccessSettings) {
		this.flagHasSendedPasswordToAccessSettings = flagHasSendedPasswordToAccessSettings;
	}

	public boolean hasTriedToAccessSettings() {
		return flagHasTriedToAccessSettings;
	}

	public void setHasTriedToAccessSettings(boolean hasTriedToAccessSettings) {
		this.flagHasTriedToAccessSettings = hasTriedToAccessSettings;
	}
}

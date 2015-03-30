package com.lqc.mylocalguide.utilities;


import android.app.Application;

public class CustomApplicationClass extends Application {
	
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
		mInstance = this;
		isTryingToExitApplication = false;
		flagHasTriedToAccessSettings = false;
		flagHasSendedPasswordToAccessSettings = false;
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

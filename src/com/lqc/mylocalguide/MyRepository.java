package com.lqc.mylocalguide;

import android.app.Application;

public class MyRepository extends Application {
	
	private String USER_PASSWORD = "user";
	private String ADMIN_PASSWORD = "admin";

	public String getUSER_PASSWORD() {
		return USER_PASSWORD;
	}

	public void setUSER_PASSWORD(String uSER_PASSWORD) {
		USER_PASSWORD = uSER_PASSWORD;
	}

	public String getADMIN_PASSWORD() {
		return ADMIN_PASSWORD;
	}

	public void setADMIN_PASSWORD(String aDMIN_PASSWORD) {
		ADMIN_PASSWORD = aDMIN_PASSWORD;
	}

	public static MyRepository mInstance;
	
	private String url = "http://www.my-local.guide";
	private int scalePercentage = 100;

	public int getScale() {
		return scalePercentage;
	}

	public void setScale(int scale) {
		this.scalePercentage = scale;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance  = this;
	}

	public static MyRepository get() {
		return mInstance;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}

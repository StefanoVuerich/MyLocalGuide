package com.lqc.mylocalguide;

import android.app.Application;

public class MyRepository extends Application {
	
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

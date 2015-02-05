package com.lqc.mylocalguide.scaling;

import android.content.Context;

import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class ScalingHandler {
	
	private static ScalingHandler instance;
	private int minScaling = 0;
	private int maxScaling = 200;
	private int scaleDifference = 10;
	
	public int getScaleDifference() {
		return scaleDifference;
	}

	public void setScaleDifference(int scaleDifference) {
		this.scaleDifference = scaleDifference;
	}

	private ScalingHandler() {}
	
	public int getMinScaling() {
		return minScaling;
	}

	public void setMinScaling(int minScaling) {
		this.minScaling = minScaling;
	}

	public int getMaxScaling() {
		return maxScaling;
	}

	public void setMaxScaling(int maxScaling) {
		this.maxScaling = maxScaling;
	}

	public static ScalingHandler getInstance() {
		if(instance == null) 
			instance = new ScalingHandler();
		return instance;
	}
	
	public void updateScale(Context context, int scale) {
		ConfigurationStorage.getInstance().updateZoom(context, scale);
	}
}

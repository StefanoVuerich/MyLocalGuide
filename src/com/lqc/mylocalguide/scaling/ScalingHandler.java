package com.lqc.mylocalguide.scaling;

import android.content.Context;

import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class ScalingHandler {
	
	private static ScalingHandler instance;
	private final static int MINSCALING = 0;
	private final static int MAXSCALING = 200;
	private int scaleDifference = 10;
	
	public int getScaleDifference() {
		return scaleDifference;
	}

	public void setScaleDifference(int scaleDifference) {
		this.scaleDifference = scaleDifference;
	}

	private ScalingHandler() {}
	
	public int getMinScaling() {
		return MINSCALING;
	}

	public int getMaxScaling() {
		return MAXSCALING;
	}

	public static ScalingHandler getInstance() {
		if(instance == null) 
			instance = new ScalingHandler();
		return instance;
	}
	
	public void updateScale(Context context, int scale) {
		if(scale < MINSCALING)
			scale = MINSCALING;
		
		if(scale > MAXSCALING)
			scale= MAXSCALING;
		
		ConfigurationStorage.getInstance().updateZoom(context, scale);
	}
}

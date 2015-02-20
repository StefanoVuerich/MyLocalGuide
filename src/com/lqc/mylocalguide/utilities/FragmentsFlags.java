package com.lqc.mylocalguide.utilities;

public enum FragmentsFlags {

	WEBVIEWFRAGMENT(0), ADMINISTRATIONFRAGMENT(1);
	
	private int position;
	
	public int getPosition() {
		return this.position;
	}

	FragmentsFlags(int position) {
		this.position = position;
	}
}

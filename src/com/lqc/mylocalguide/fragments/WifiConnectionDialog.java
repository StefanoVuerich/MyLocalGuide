package com.lqc.mylocalguide.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class WifiConnectionDialog extends DialogFragment {

	OnConnectionHandler mCallback;
	private final static String WIFIFIELDS = "WifiFields";

	public static WifiConnectionDialog getInstance(String[] wifiFields) {
		WifiConnectionDialog dialog = new WifiConnectionDialog();
		Bundle vBundle = new Bundle();
		vBundle.putStringArray(WIFIFIELDS, wifiFields);
		dialog.setArguments(vBundle);
		return dialog;
	}

	public interface OnConnectionHandler {
		public void OnButtonPress(int which);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof OnConnectionHandler) {
			mCallback = (OnConnectionHandler) activity;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder vBuilder = new AlertDialog.Builder(getActivity());

		Bundle vBundle = getArguments();
		if (vBundle != null) {
			String[] wifiProprieties = vBundle.getStringArray(WIFIFIELDS);
			vBuilder.setTitle(wifiProprieties[0])
					.setMessage("Status: " + wifiProprieties[1])
					.setMessage("Signal stringth: " + wifiProprieties[2])
					.setMessage("Link speed: " + wifiProprieties[3])
					.setMessage("Security: " + wifiProprieties[4])
					.setMessage("IP adress: " + wifiProprieties[5]);

			Dialog vDialog = vBuilder.create();

			return vDialog;
		}
		return null;
	}

}

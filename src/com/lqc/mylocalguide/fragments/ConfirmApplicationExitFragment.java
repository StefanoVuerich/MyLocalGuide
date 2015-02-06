package com.lqc.mylocalguide.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.lqc.mylocalguide.R;

public class ConfirmApplicationExitFragment extends DialogFragment {

	private final static String TAG = "ConfirmApplicationExitFragment";

	public static String getTAG() {
		return TAG;
	}

	private IExitApplicationConfirm mCallback;

	public interface IExitApplicationConfirm {

		public void onExitApplication();
		public void onCancelExit();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof IExitApplicationConfirm) {
			mCallback = (IExitApplicationConfirm) activity;
		}
	}

	public static ConfirmApplicationExitFragment get() {
		ConfirmApplicationExitFragment exitApplicationDialog = new ConfirmApplicationExitFragment();
		return exitApplicationDialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View rootView = inflater.inflate(R.layout.confirm_exit_dialog,
				container, false);

		View exitAppBtn = rootView.findViewById(R.id.exitAppBtn);
		View cancelExitAppBtn = rootView.findViewById(R.id.cancelExitAppBtn);

		final Button exitAppBtnCst = ((Button) exitAppBtn);
		final Button cancelExitAppBtnCst = ((Button) cancelExitAppBtn);

		exitAppBtnCst.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onExitApplication();
			}
		});

		cancelExitAppBtnCst.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onCancelExit();
			}
		});

		return rootView;
	}
}

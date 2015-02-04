package com.lqc.mylocalguide.fragments;

import com.lqc.mylocalguide.fragments.CheckPasswordDialog.ICheckPassword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo);
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder vBuilder = new AlertDialog.Builder(getActivity());
		
		vBuilder.setTitle("Confirm Application Exit");

		vBuilder.setMessage("Do you really want to qui the application ?");

		vBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mCallback != null)
					mCallback.onExitApplication();
			}
		});

		vBuilder.setNegativeButton("CANCEL",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mCallback != null)
							mCallback.onCancelExit();
					}
				});

		Dialog vDialog = vBuilder.create();
		return vDialog;
	}
}

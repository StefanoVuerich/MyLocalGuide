package com.lqc.mylocalguide.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lqc.mylocalguide.R;

public class CheckSettingsPasswordDialog extends DialogFragment {

	private View rootView;
	public static final String TAG = "CheckPasswordDialog";
	private static final String CHECKSETTINGSPASSWORD_DIALOG_FRAGMENT_FLAG = "CheckSettingsPasswordDialogFragmentFLAG";
	private String mode_FLAG;
	private ICheckSettingsPassword mCallback;

	public interface ICheckSettingsPassword {

		public void onSettingsCheckPassword(String password);
		public void onSettingsCancel();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ICheckSettingsPassword) {
			mCallback = (ICheckSettingsPassword) activity;
		}
	}

	public static CheckSettingsPasswordDialog get(String flag) {
		CheckSettingsPasswordDialog checkPasswordDialog = new CheckSettingsPasswordDialog();
		Bundle vBundle = new Bundle();
		vBundle.putString(CHECKSETTINGSPASSWORD_DIALOG_FRAGMENT_FLAG, flag);
		checkPasswordDialog.setArguments(vBundle);
		return checkPasswordDialog;
	}
	
	public void shake() {
		LinearLayout checkPasswordFragmentLayout = (LinearLayout)rootView.findViewById(R.id.checkPasswordLayout);
		Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
		checkPasswordFragmentLayout.startAnimation(shake);
	}
	
	public void clearPasswordEditText() {
		EditText passwordEditExt = (EditText)rootView.findViewById(R.id.insertPasswordEditTxt);
		passwordEditExt.setText("");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle vBundle = getArguments();
		if (vBundle != null) {
			mode_FLAG = vBundle.getString(CHECKSETTINGSPASSWORD_DIALOG_FRAGMENT_FLAG);
		}
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		rootView = inflater.inflate(R.layout.check_password_dialog_layout,
				container, false);
		
		View passwordEditText = rootView.findViewById(R.id.insertPasswordEditTxt);
		View loginBtn = rootView.findViewById(R.id.loginBtn);
		View cancelBtn = rootView.findViewById(R.id.cancelBtn);
		
		final EditText passwordEditTextCst = ((EditText) passwordEditText);
		final Button cancelBtnCst = ((Button) cancelBtn);
		final Button loginBtnCst = ((Button) loginBtn);
		
		loginBtnCst.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onSettingsCheckPassword(passwordEditTextCst.getText().toString());;
			}
		});
		
		cancelBtnCst.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onSettingsCancel();
			}
		});

		return rootView;
	}
}

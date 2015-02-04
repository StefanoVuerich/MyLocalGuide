package com.lqc.mylocalguide.fragments;

import com.lqc.mylocalguide.R;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CheckPasswordDialog extends DialogFragment {

	public static final String TAG = "CheckPasswordDialog";
	public static final String CHECKPASSWORD_DIALOG_FRAGMENT_FLAG = "CheckPasswordDialogFragmentFLAG";
	private String mode_FLAG;

	private ICheckPassword mCallback;

	public interface ICheckPassword {

		public void onLogin(String mode_flag, String password);
		public void onCancel();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof ICheckPassword) {
			mCallback = (ICheckPassword) activity;
		}
	}

	public static CheckPasswordDialog get(String flag) {
		CheckPasswordDialog checkPasswordDialog = new CheckPasswordDialog();
		Bundle vBundle = new Bundle();
		vBundle.putString(CHECKPASSWORD_DIALOG_FRAGMENT_FLAG, flag);
		checkPasswordDialog.setArguments(vBundle);
		return checkPasswordDialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle vBundle = getArguments();
		if (vBundle != null) {
			mode_FLAG = vBundle.getString(CHECKPASSWORD_DIALOG_FRAGMENT_FLAG);

			Toast.makeText(getActivity(), mode_FLAG, Toast.LENGTH_LONG).show();
		}
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View v = inflater.inflate(R.layout.check_password_dialog_layout,
				container, false);
		
		View passwordEditText = v.findViewById(R.id.insertPasswordEditTxt);
		View loginBtn = v.findViewById(R.id.loginBtn);
		View cancelBtn = v.findViewById(R.id.cancelBtn);
		
		final EditText passwordEditTextCst = ((EditText) passwordEditText);
		final Button cancelBtnCst = ((Button) cancelBtn);
		final Button loginBtnCst = ((Button) loginBtn);
		
		loginBtnCst.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onLogin(mode_FLAG, passwordEditTextCst.getText().toString());;
			}
		});
		
		cancelBtnCst.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onCancel();
			}
		});

		return v;
	}
}

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

public class CheckPasswordDialog extends DialogFragment implements TextWatcher{
	
	private ICheckPassword mCallback;

	public interface ICheckPassword {	
		
		public void checkPassword(String password);
		public void onCancel();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if(activity instanceof ICheckPassword) {
			mCallback = (ICheckPassword)activity;
		}
	}
	
	public static CheckPasswordDialog get() {
		CheckPasswordDialog checkPasswordDialog = new CheckPasswordDialog();
		return checkPasswordDialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View v = inflater.inflate(R.layout.check_password_dialog_layout, container, false);
		View passwordLabel = v.findViewById(R.id.insertPasswordLabel);
		View passwordEditText = v.findViewById(R.id.insertPasswordEditTxt);
		View cancelBtn = v.findViewById(R.id.cancelBtn);
		final EditText passwordEditTextCst = ((EditText)passwordEditText);
		final Button cancelBtnCst = ((Button)cancelBtn);
		
		passwordEditTextCst.addTextChangedListener(this);	
		cancelBtnCst.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCallback.onCancel();
			}
		});
		
		return v;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(mCallback != null) {
			mCallback.checkPassword(s.toString());
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}
}

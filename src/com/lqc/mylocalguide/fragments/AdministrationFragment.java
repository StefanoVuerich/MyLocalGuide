package com.lqc.mylocalguide.fragments;

import com.lqc.mylocalguide.MyRepository;
import com.lqc.mylocalguide.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AdministrationFragment extends Fragment {

	private EditText urlEditTxt;
	private Button save, cancel;
	private OnActionSelected mCallback;

	public interface OnActionSelected {

		public void OnSave();
		public void OnCancel();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof OnActionSelected) {
			mCallback = (OnActionSelected) activity;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.manager_fragment_layout,
				container, false);

		urlEditTxt = (EditText) rootView.findViewById(R.id.urlEditText);
		urlEditTxt.setText(MyRepository.get().getUrl());

		save = (Button) rootView.findViewById(R.id.saveChangesBtn);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				savePassword();
				if (mCallback != null) {
					mCallback.OnSave();
				}
			}
		});

		cancel = (Button) rootView.findViewById(R.id.cancelChangesBtn);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.OnCancel();
			}
		});

		return rootView;
	}

	private void savePassword() {
		String url = "";
		if ((url = urlEditTxt.getText().toString()) != null
				&& !(url.equals(MyRepository.get().getUrl()))) {
			MyRepository.get().setUrl(url);
		}
	}
}

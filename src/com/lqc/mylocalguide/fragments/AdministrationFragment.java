package com.lqc.mylocalguide.fragments;

import com.lqc.mylocalguide.MyRepository;
import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.scaling.ScalingHandler;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ZoomControls;

public class AdministrationFragment extends Fragment {

	private EditText urlEditTxt, newAdminPassword, confirmNewAdminPassword,
			newUserPassword, confirmNewUserPassword;
	private Button save, cancel, exitApp;
	private OnActionSelected mCallback;
	private TextView zoomPercentage;
	private ZoomControls zoomControls;
	int currentScale;
	private final static String ADMINISTRATION_FRAGMENT_FLAG = "AdministrationFragmentFLAG";

	public AdministrationFragment getInstance(String flag) {

		AdministrationFragment administrationFragment = new AdministrationFragment();
		Bundle vBundle = new Bundle();
		vBundle.putString(ADMINISTRATION_FRAGMENT_FLAG, flag);
		administrationFragment.setArguments(vBundle);
		return administrationFragment;
	}

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

		currentScale = MyRepository.get().getScale();

		urlEditTxt = (EditText) rootView.findViewById(R.id.urlEditText);
		urlEditTxt.setText(MyRepository.get().getUrl());

		zoomPercentage = (TextView) rootView.findViewById(R.id.zoomPercentage);
		zoomPercentage.setText("" + MyRepository.get().getScale());

		zoomControls = (ZoomControls) rootView.findViewById(R.id.zoomControls);
		zoomControls.setOnZoomInClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				zoomIn();
			}

		});
		zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				zoomOut();
			}
		});
		
		newAdminPassword = (EditText)rootView.findViewById(R.id.newAdminPassTxtEdit);
		confirmNewAdminPassword = (EditText)rootView.findViewById(R.id.confirmNewAdminPassTxtEdit);
		newUserPassword = (EditText)rootView.findViewById(R.id.newUserPassTxtEdit);
		confirmNewUserPassword = (EditText)rootView.findViewById(R.id.confirmNewUserPassTxtEdit);
		
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

		exitApp = (Button) rootView.findViewById(R.id.exitAppButton);
		exitApp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showConfirmExitDialog();
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

	private void zoomIn() {

		int maxScaling = ScalingHandler.getInstance().getMaxScaling();
		if (maxScaling - currentScale >= 10) {
			int newScale = currentScale
					+ ScalingHandler.getInstance().getScaleDifference();
			MyRepository.get().setScale(newScale);
			zoomPercentage.setText("" + newScale);
		}
	}

	private void zoomOut() {

		if (currentScale >= 10) {
			int newScale = currentScale
					- ScalingHandler.getInstance().getScaleDifference();
			MyRepository.get().setScale(newScale);
			zoomPercentage.setText("" + newScale);
		}
	}

	private void showConfirmExitDialog() {
		ConfirmApplicationExitFragment confirmExitDialog = ConfirmApplicationExitFragment
				.get();
		confirmExitDialog.show(getFragmentManager(),
				ConfirmApplicationExitFragment.getTAG());

	}
}

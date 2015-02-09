package com.lqc.mylocalguide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.login.PasswordHandler;
import com.lqc.mylocalguide.scaling.ScalingHandler;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class AdministrationFragment extends Fragment {

	SharedPreferences settings;
	private EditText zoomPercentageEditTxt, urlEditTxt, newAdminPasswordTxt,
			confirmNewAdminPasswordTxt, newUserPasswordTxt,
			confirmNewUserPasswordTxt;
	private Button save, cancel, exitApp;
	private OnActionSelected mCallback;
	private TextView zoomPercentage;
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

		public void onSave();

		public void onCancelSave();
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

		final View rootView = inflater.inflate(
				R.layout.manager_fragment_layout, container, false);

		settings = getActivity().getSharedPreferences(
				ConfigurationStorage.getInstance().STORAGE, 0);

		urlEditTxt = (EditText) rootView.findViewById(R.id.urlEditText);
		urlEditTxt.setText(settings.getString(ConfigurationStorage.URL, ""));

		zoomPercentage = (TextView) rootView.findViewById(R.id.zoomPercentage);
		zoomPercentage.setText(""
				+ settings.getInt(ConfigurationStorage.ZOOM, 0));
		zoomPercentageEditTxt = (EditText) rootView
				.findViewById(R.id.zoomPercentageEditTxt);

		newAdminPasswordTxt = (EditText) rootView
				.findViewById(R.id.newAdminPassTxtEdit);
		confirmNewAdminPasswordTxt = (EditText) rootView
				.findViewById(R.id.confirmNewAdminPassTxtEdit);
		newUserPasswordTxt = (EditText) rootView
				.findViewById(R.id.newUserPassTxtEdit);
		confirmNewUserPasswordTxt = (EditText) rootView
				.findViewById(R.id.confirmNewUserPassTxtEdit);

		save = (Button) rootView.findViewById(R.id.saveChangesBtn);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// get all variables
				String url = urlEditTxt.getText().toString();
				String zoom = zoomPercentageEditTxt.getText().toString();
				String newAdminPassword = newAdminPasswordTxt.getText()
						.toString();
				String confirmNewAdminPassword = confirmNewAdminPasswordTxt
						.getText().toString();
				String newUserPassword = newUserPasswordTxt.getText()
						.toString();
				String confirmNewUserPassword = confirmNewUserPasswordTxt
						.getText().toString();
				int parsedZoom = 0;
				if (!zoom.equals(""))
					parsedZoom = Integer.parseInt(zoom);

				// update url
				if (!(isEmptyString(url))
						&& !(url.equals(settings.getString(
								ConfigurationStorage.URL, "")))) {
					updateUrl(url);
				}

				// update zoom
				if (!(isEmptyString(zoom))
						&& parsedZoom != settings.getInt(
								ConfigurationStorage.ZOOM, 0)) {
					updateZoom(parsedZoom);
				}

				boolean adminPasswordSaved = false;
				// update admin password
				if (!(isEmptyString(newAdminPassword) && isEmptyString(confirmNewAdminPassword))) {
					adminPasswordSaved = updateAdminPassword(newAdminPassword,
							confirmNewAdminPassword);
				} else {
					resetFeedback(0);
					adminPasswordSaved = true;
				}

				boolean userPasswordSaved = false;
				// update user password
				if (!(isEmptyString(newUserPassword) && isEmptyString(confirmNewUserPassword))) {
					userPasswordSaved = updateUserPassword(newUserPassword,
							confirmNewUserPassword);
				} else {
					resetFeedback(1);
					userPasswordSaved = true;
				}

				if (mCallback != null) {
					if (adminPasswordSaved && userPasswordSaved)
						mCallback.onSave();
				}
			}

			// 0 is admin, 1 is user
			private void resetFeedback(int which) {
				int feedbackToReset = 0;
				if (which == 0) {
					feedbackToReset = R.id.newAdminPasswordFeedback;
				} else {
					feedbackToReset = R.id.newUserPasswordFeedback;
				}
				TextView feedback = (TextView) rootView
						.findViewById(feedbackToReset);
				feedback.setText("");
			}

			private boolean updateAdminPassword(String password,
					String confirmPassword) {
				return PasswordHandler.getInstance().changeAdminPassword(
						AdministrationFragment.this, getActivity(), password,
						confirmPassword);
			}

			private boolean updateUserPassword(String password,
					String confirmPassword) {
				return PasswordHandler.getInstance().changeUserPassword(
						AdministrationFragment.this, getActivity(), password,
						confirmPassword);
			}

			private void updateZoom(int zoom) {
				ScalingHandler.getInstance().updateScale(getActivity(), zoom);
			}

			private void updateUrl(String url) {
				ConfigurationStorage.getInstance()
						.updateUrl(getActivity(), url);
			}
		});

		cancel = (Button) rootView.findViewById(R.id.cancelChangesBtn);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.onCancelSave();
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

	private void showConfirmExitDialog() {
		ConfirmApplicationExitFragment confirmExitDialog = ConfirmApplicationExitFragment
				.get();
		confirmExitDialog.show(getFragmentManager(),
				ConfirmApplicationExitFragment.getTAG());
	}

	private boolean isEmptyString(String stringToEvaluate) {

		if (stringToEvaluate.equals(""))
			return true;

		return false;
	}
}

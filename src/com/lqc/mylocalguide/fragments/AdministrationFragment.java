package com.lqc.mylocalguide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.login.PasswordHandler;
import com.lqc.mylocalguide.scaling.ScalingHandler;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class AdministrationFragment extends Fragment implements OnTouchListener {

	public final static String ADMINISTRATION_FRAGMENT_FLAG = "AdministrationFragmentFLAG";
	public final static String _TAG = AdministrationFragment.class
			.getSimpleName();
	// Url
	private static final String URL_SCHEME = "http://";
	private static final String APPLICATION_URL_TAG = "APPLICATION_URL_TAG";
	// Zoom
	private static final String ZOOM_TAG = "ZOOM_TAG";
	int currentScale;
	// Admin password change variables
	private static final String ADMIN_FEEDBACK = "AdminFeedback";
	private static final String CONFIRM_NEW_ADMIN_PASSWORD_TAG = "CONFIRM_NEW_ADMIN_PASSWORD_TAG";
	private static final String NEW_ADMIN_PASSWORD_TAG = "NEW_ADMIN_PASSWORD_TAG";
	// User password change variables
	private static final String USER_FEEDBACK = "UserFeedback";
	private static final String CONFIRM_NEW_USER_PASSWORD_TAG = "CONFIRM_NEW_USER_PASSWORD_TAG";
	private static final String NEW_USER_PASSWORD_TAG = "NEW_USER_PASSWORD_TAG";
	// Shared preferences
	SharedPreferences settings;
	// Callbacks
	private OnActionSelected mCallback;
	// Views
	private EditText zoomPercentageEditTxt, urlEditTxt, newAdminPasswordTxt,
			confirmNewAdminPasswordTxt, newUserPasswordTxt,
			confirmNewUserPasswordTxt;
	private Button save, cancel, exitApp;
	private TextView adminFeedback, userFeedback;
	private View rootView;
	private LinearLayout administrationFragmentContainer;


	public static AdministrationFragment getInstance() {
		AdministrationFragment administrationFragment = new AdministrationFragment();
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

		rootView = inflater.inflate(R.layout.manager_fragment_layout,
				container, false);

		administrationFragmentContainer = (LinearLayout) rootView
				.findViewById(R.id.administrationFragmentContainerLayout);
		administrationFragmentContainer.setOnTouchListener(this);

		initView();

		if (savedInstanceState != null)
			restoreState(savedInstanceState);
		else
			urlEditTxt
					.setText(settings.getString(ConfigurationStorage.URL, ""));
		zoomPercentageEditTxt.setText(""
				+ settings.getInt(ConfigurationStorage.ZOOM, 0));

		

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void restoreState(Bundle savedInstanceState) {
		urlEditTxt.setText(savedInstanceState.getString(APPLICATION_URL_TAG));
		zoomPercentageEditTxt.setText(savedInstanceState.getString(ZOOM_TAG));
		newAdminPasswordTxt.setText(savedInstanceState
				.getString(NEW_ADMIN_PASSWORD_TAG));
		confirmNewAdminPasswordTxt.setText(savedInstanceState
				.getString(CONFIRM_NEW_ADMIN_PASSWORD_TAG));
		newUserPasswordTxt.setText(savedInstanceState
				.getString(NEW_USER_PASSWORD_TAG));
		confirmNewUserPasswordTxt.setText(savedInstanceState
				.getString(CONFIRM_NEW_USER_PASSWORD_TAG));
		adminFeedback.setText(savedInstanceState.getString(ADMIN_FEEDBACK));
		userFeedback.setText(savedInstanceState.getString(USER_FEEDBACK));
	}

	private void initView() {
		urlEditTxt = (EditText) rootView.findViewById(R.id.urlEditText);
		settings = getActivity().getSharedPreferences(
				ConfigurationStorage.getInstance().STORAGE, 0);
		// Zoom section
		zoomPercentageEditTxt = (EditText) rootView
				.findViewById(R.id.zoomPercentageEditTxt);
		// Change admin password section
		newAdminPasswordTxt = (EditText) rootView
				.findViewById(R.id.newAdminPassTxtEdit);
		confirmNewAdminPasswordTxt = (EditText) rootView
				.findViewById(R.id.confirmNewAdminPassTxtEdit);
		adminFeedback = (TextView) rootView
				.findViewById(R.id.newAdminPasswordFeedback);
		// Change user password section
		newUserPasswordTxt = (EditText) rootView
				.findViewById(R.id.newUserPassTxtEdit);
		confirmNewUserPasswordTxt = (EditText) rootView
				.findViewById(R.id.confirmNewUserPassTxtEdit);
		userFeedback = (TextView) rootView
				.findViewById(R.id.newUserPasswordFeedback);
		// Button section
		save = (Button) rootView.findViewById(R.id.saveChangesBtn);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
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
	}

	private void save() {

		// get all variables
		String url = urlEditTxt.getText().toString();
		String zoom = zoomPercentageEditTxt.getText().toString();
		String newAdminPassword = newAdminPasswordTxt.getText().toString();
		String confirmNewAdminPassword = confirmNewAdminPasswordTxt.getText()
				.toString();
		String newUserPassword = newUserPasswordTxt.getText().toString();
		String confirmNewUserPassword = confirmNewUserPasswordTxt.getText()
				.toString();
		int parsedZoom = 0;
		if (!zoom.equals(""))
			parsedZoom = Integer.parseInt(zoom);

		// update url
		if (!(isEmptyString(url))
				&& !(url.equals(settings
						.getString(ConfigurationStorage.URL, "")))) {
			updateUrl(url);
		}

		// update zoom
		if (!(isEmptyString(zoom))
				&& parsedZoom != settings.getInt(ConfigurationStorage.ZOOM, 0)) {
			updateZoom(parsedZoom);
		}

		// update admin password
		boolean adminPasswordSaved = false;
		if (!(isEmptyString(newAdminPassword) && isEmptyString(confirmNewAdminPassword))) {
			adminPasswordSaved = updateAdminPassword(newAdminPassword,
					confirmNewAdminPassword);
		} else {
			resetFeedback(0);
			adminPasswordSaved = true;
		}

		if (mCallback != null) {
			if (adminPasswordSaved/* && userPasswordSaved */)
				mCallback.onSave();
		}

		// update user password
		boolean userPasswordSaved = false;
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

	// 0 is admin, 1 is user
		private void resetFeedback(int which) {
			int feedbackToReset = 0;
			if (which == 0) {
				feedbackToReset = R.id.newAdminPasswordFeedback;
			} else {
				feedbackToReset = R.id.newUserPasswordFeedback;
			}
			TextView feedback = (TextView) rootView.findViewById(feedbackToReset);
			feedback.setText("");
		}

	private boolean updateAdminPassword(String password, String confirmPassword) {

		return PasswordHandler.getInstance().changeAdminPassword(getActivity(),
				password, confirmPassword);
	}
	
	private boolean updateUserPassword(String password, String confirmPassword) {
		return PasswordHandler.getInstance().changeUserPassword(getActivity(),
				password, confirmPassword);
	}

	private void updateZoom(int zoom) {
		ScalingHandler.getInstance().updateScale(getActivity(), zoom);
	}

	private void updateUrl(String url) {

		// check is URL starts with http://
		if (!url.startsWith(URL_SCHEME)) {
			url = URL_SCHEME + url;
		}
		ConfigurationStorage.getInstance().updateUrl(getActivity(), url);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Bundle vBundle = new Bundle();
		vBundle.putString(APPLICATION_URL_TAG, urlEditTxt.getText().toString());
		vBundle.putString(ZOOM_TAG, zoomPercentageEditTxt.getText().toString());
		vBundle.putString(NEW_ADMIN_PASSWORD_TAG, newAdminPasswordTxt.getText()
				.toString());
		vBundle.putString(CONFIRM_NEW_ADMIN_PASSWORD_TAG,
				confirmNewAdminPasswordTxt.getText().toString());
		vBundle.putString(NEW_USER_PASSWORD_TAG, newUserPasswordTxt.getText()
				.toString());
		vBundle.putString(CONFIRM_NEW_USER_PASSWORD_TAG,
				confirmNewUserPasswordTxt.getText().toString());
		// saving feedbacks*/
		vBundle.putString(ADMIN_FEEDBACK, adminFeedback.getText().toString());
		vBundle.putString(USER_FEEDBACK, userFeedback.getText().toString());
		outState.putAll(vBundle);

		outState.putString(_TAG, "Restore");
	}

	// Hide virtual keyboard when touch container layout
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		hideVirtualKeyboard();
		return false;
	}

	public void hideVirtualKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(getActivity().INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				administrationFragmentContainer.getWindowToken(), 2);
	}

	@Override
	public void onStop() {
		super.onStop();
		hideVirtualKeyboard();
	}

}

package com.lqc.mylocalguide.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.login.PasswordHandler;
import com.lqc.mylocalguide.scaling.ScalingHandler;
import com.lqc.mylocalguide.storage.ConfigurationStorage;
import com.lqc.mylocalguide.utilities.WifiListAdapter;
import com.lqc.mylocalguide.wifimanagement.MyWifiManager;

public class AdministrationFragment extends Fragment implements OnTouchListener {

	public final static String _TAG = "AdministrationFragment";

	private static final String URL_SCHEME = "http://";
	private static final String ADMIN_FEEDBACK = "AdminFeedback";
	private static final String CONFIRM_NEW_ADMIN_PASSWORD_TAG = "CONFIRM_NEW_ADMIN_PASSWORD_TAG";
	private static final String NEW_ADMIN_PASSWORD_TAG = "NEW_ADMIN_PASSWORD_TAG";
	private static final String ZOOM_TAG = "ZOOM_TAG";
	private static final String APPLICATION_URL_TAG = "APPLICATION_URL_TAG";
	SharedPreferences settings;
	private EditText zoomPercentageEditTxt, urlEditTxt, newAdminPasswordTxt,
			confirmNewAdminPasswordTxt;
	private Button save, cancel, exitApp;
	private OnActionSelected mCallback;
	private TextView adminFeedback;
	private View rootView;
	int currentScale;
	public final static String ADMINISTRATION_FRAGMENT_FLAG = "AdministrationFragmentFLAG";
	private LinearLayout administrationFragmentContainer;
	private ListView wifiConnectionsListView;
	private Switch wifiEnabledSwitch;

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

	ArrayList<ScanResult> mArray;
	
	public void printWifiList() {
		mArray = MyWifiManager.results;
		ListView wifiList = (ListView)rootView.findViewById(R.id.wifiConnectionsList);
		WifiListAdapter adapter = new WifiListAdapter(getActivity(), mArray);
		wifiList.setAdapter(adapter);
		wifiList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				/*
				 * restart from here
				 * 
				ScanResult wifiProprieties = mArray.get(position);
				String[] stringProprieties = new String[6];
				stringProprieties[0] = wifiProprieties.SSID;
				stringProprieties[1] = wifiProprieties.;
				stringProprieties[2] = wifiProprieties.SSID;
				stringProprieties[3] = wifiProprieties.SSID;
				stringProprieties[4] = wifiProprieties.SSID;
				stringProprieties[5] = wifiProprieties.SSID;
				
				*/
			}
		});
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
		
		wifiEnabledSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked) {
					wifiManager = new MyWifiManager(getActivity());
					wifiManager.startWifiScan();
				} else {
					if(wifiManager != null) {
						wifiManager.stopWifiScan();
					}
				}
			}
		});

		return rootView;
	}
	
	MyWifiManager wifiManager;

	@Override
	public void onResume() {
		super.onResume();
		if(wifiManager != null) {
			wifiManager.startWifiScan();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(wifiManager != null) {
			wifiManager.stopWifiScan();
		}
	}

	private void restoreState(Bundle savedInstanceState) {
		urlEditTxt.setText(savedInstanceState.getString(APPLICATION_URL_TAG));
		zoomPercentageEditTxt.setText(savedInstanceState.getString(ZOOM_TAG));
		newAdminPasswordTxt.setText(savedInstanceState
				.getString(NEW_ADMIN_PASSWORD_TAG));
		confirmNewAdminPasswordTxt.setText(savedInstanceState
				.getString(CONFIRM_NEW_ADMIN_PASSWORD_TAG));
		adminFeedback.setText(savedInstanceState.getString(ADMIN_FEEDBACK));
	}

	private void initView() {
		urlEditTxt = (EditText) rootView.findViewById(R.id.urlEditText);
		settings = getActivity().getSharedPreferences(
				ConfigurationStorage.getInstance().STORAGE, 0);
		//Wifi section
		wifiEnabledSwitch = (Switch)rootView.findViewById(R.id.switchWifiEnabled);
		wifiConnectionsListView = (ListView) rootView.findViewById(R.id.wifiConnectionsList);
		//Zoom section
		zoomPercentageEditTxt = (EditText) rootView
				.findViewById(R.id.zoomPercentageEditTxt);
		//Change password section
		newAdminPasswordTxt = (EditText) rootView
				.findViewById(R.id.newAdminPassTxtEdit);
		confirmNewAdminPasswordTxt = (EditText) rootView
				.findViewById(R.id.confirmNewAdminPassTxtEdit);
		adminFeedback = (TextView) rootView
				.findViewById(R.id.newAdminPasswordFeedback);
		//Button section
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

		boolean adminPasswordSaved = false;
		// update admin password
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

	private void resetFeedback(int which) {
		TextView feedback = (TextView) rootView
				.findViewById(R.id.newAdminPasswordFeedback);
		feedback.setText("");
	}

	private boolean updateAdminPassword(String password, String confirmPassword) {

		return PasswordHandler.getInstance().changeAdminPassword(getActivity(),
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
		// saving feedbacks*/
		vBundle.putString(ADMIN_FEEDBACK, adminFeedback.getText().toString());
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

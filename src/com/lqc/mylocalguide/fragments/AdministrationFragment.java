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

	private EditText urlEditTxt;
	private Button save, cancel;
	private OnActionSelected mCallback;
	private TextView zoomPercentage;
	private ZoomControls zoomControls;

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

	private void zoomIn() {

		int currentScale = MyRepository.get().getScale();
		int maxScaling = ScalingHandler.getInstance().getMaxScaling();
		if (maxScaling - currentScale >= 10) {
			int newScale = currentScale
					+ ScalingHandler.getInstance().getScaleDifference();
			MyRepository.get().setScale(newScale);
			zoomPercentage.setText("" + newScale);
		}
	}

	private void zoomOut() {
		int currentScale = MyRepository.get().getScale();
		int maxScaling = ScalingHandler.getInstance().getMaxScaling();
		if (currentScale >= 10) {
			int newScale = currentScale
					- ScalingHandler.getInstance().getScaleDifference();
			MyRepository.get().setScale(newScale);
			zoomPercentage.setText("" + newScale);
		}
	}
}

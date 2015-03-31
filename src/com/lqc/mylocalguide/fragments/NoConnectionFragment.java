package com.lqc.mylocalguide.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lqc.mylocalguide.MainActivity;
import com.lqc.mylocalguide.R;

public class NoConnectionFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.no_internet_fregment_layout,
				container, false);
		
		((ImageView) rootView.findViewById(R.id.exitNoInternetFragment))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showCheckPasswordDialog("admin");
					}
				});
		return rootView;
	}

	public static NoConnectionFragment getInstance() {
		return new NoConnectionFragment();
	}

	private void showCheckPasswordDialog(String flag) {
		((MainActivity)getActivity()).task.cancel(false);
		CheckPasswordDialog checkPasswordDialog = CheckPasswordDialog.get(flag);
		checkPasswordDialog.show(getFragmentManager(), CheckPasswordDialog.TAG);
	}
}

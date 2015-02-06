package com.lqc.mylocalguide.feedbacks;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class ErrorHandlerNoIUFragment extends Fragment {

	public final static String TAG = "ErrorHandler";
	public final static String BUNDLETAG = "BundleTag";
	private IErrorHandler mCallback;

	// 0 is admin, 1 is user
	public static ErrorHandlerNoIUFragment getInstance(int who) {
		ErrorHandlerNoIUFragment fragment = new ErrorHandlerNoIUFragment();
		Bundle vBundle = new Bundle();
		vBundle.putInt(BUNDLETAG, who);
		fragment.setArguments(vBundle);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof IErrorHandler) {
			mCallback = (IErrorHandler) activity;
			
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle mBundle = getArguments();
		if(mBundle != null){
			if(mCallback != null) {
				mCallback.onPasswordDoesNotMatchError(mBundle.getInt(BUNDLETAG));
			}
		}
	}

	public interface IErrorHandler {
		public void onPasswordDoesNotMatchError(int who);
	}

}

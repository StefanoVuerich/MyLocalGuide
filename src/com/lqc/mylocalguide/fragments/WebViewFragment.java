package com.lqc.mylocalguide.fragments;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebViewFragment extends Fragment {

	private WebView webView;
	private Button adminBtn, exitBtn;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.webview_fragment_layout,
				container, false);

		SharedPreferences settings = getActivity().getSharedPreferences(ConfigurationStorage.getInstance().STORAGE, 0);
		
		webView = (WebView) rootView.findViewById(R.id.mWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setInitialScale(settings.getInt(ConfigurationStorage.ZOOM, 100));
		webView.setWebViewClient(new WebViewClient() {});
		webView.loadUrl(settings.getString(ConfigurationStorage.URL, ""));

		adminBtn = (Button) rootView.findViewById(R.id.adminBtn);
		adminBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showCheckPasswordDialog("admin");
			}
		});
		
		exitBtn = (Button)rootView.findViewById(R.id.exitBtn);
		exitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showCheckPasswordDialog("exit");
			}
		});

		return rootView;
	}

	public static WebViewFragment get() {
		WebViewFragment fr = new WebViewFragment();
		return fr;
	}

	private void showCheckPasswordDialog(String flag) {

		CheckPasswordDialog checkPasswordDialog = CheckPasswordDialog.get(flag);
		checkPasswordDialog.show(getFragmentManager(), CheckPasswordDialog.TAG);
	}
}

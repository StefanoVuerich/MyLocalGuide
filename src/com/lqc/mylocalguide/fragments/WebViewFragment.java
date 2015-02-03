package com.lqc.mylocalguide.fragments;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.MyRepository;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebViewFragment extends Fragment {

	public static final String SHOW_PASSWORD_DIALOG = "SHOW_PASSWORD_DIALOG";
	private WebView webView;
	private Button adminBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// webView = (WebView) rootView.findViewById(R.id.mWebView);
		// webView.setInitialScale(50);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.webview_fragment_layout,
				container, false);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		metrics.density = 1;
		metrics.scaledDensity = 1;
		getResources().updateConfiguration(null, metrics);

		webView = (WebView) rootView.findViewById(R.id.mWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setInitialScale(MyRepository.get().getScale());
		webView.setWebViewClient(new WebViewClient() {});
		webView.loadUrl(MyRepository.get().getUrl());

		adminBtn = (Button) rootView.findViewById(R.id.adminBtn);
		adminBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showCheckPasswordDialog();
			}
		});

		return rootView;
	}

	public static WebViewFragment get() {
		WebViewFragment fr = new WebViewFragment();
		return fr;
	}

	private void showCheckPasswordDialog() {

		CheckPasswordDialog checkPasswordDialog = CheckPasswordDialog.get();
		checkPasswordDialog.show(getFragmentManager(), SHOW_PASSWORD_DIALOG);
	}
}

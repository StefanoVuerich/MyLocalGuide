package com.lqc.mylocalguide.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.customsviews.TriangleButton;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class WebViewFragment extends Fragment {

	private WebView webView;
	private ImageView adminBtn;
	public final static String _TAG = "WebViewFragment";
	private View rootView;
	private SharedPreferences settings;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.webview_fragment_layout,
				container, false);

		settings = getActivity().getSharedPreferences(
				ConfigurationStorage.getInstance().STORAGE, 0);

		setWebView();

		if (savedInstanceState != null) {
			webView.restoreState(savedInstanceState);
		} else
			webView.loadUrl(settings.getString(ConfigurationStorage.URL, ""));

		adminBtn = (ImageView) rootView.findViewById(R.id.adminBtn);
		adminBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCheckPasswordDialog("exit");
			}
		});
		adminBtn.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showCheckPasswordDialog("admin");
				return false;
			}
		});

		return rootView;
	}

	private void setWebView() {
		webView = (WebView) rootView.findViewById(R.id.mWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setInitialScale(settings.getInt(ConfigurationStorage.ZOOM, 100));
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http://kiosk.my-local.guide/_admin")) {
					if (url.endsWith("/_admin")) {
						showCheckPasswordDialog("admin");
						return true;
					}
				}
				return false;
			}
		});
	}

	public static WebViewFragment get() {
		WebViewFragment fr = new WebViewFragment();
		return fr;
	}

	private void showCheckPasswordDialog(String flag) {

		CheckPasswordDialog checkPasswordDialog = CheckPasswordDialog.get(flag);
		checkPasswordDialog.show(getFragmentManager(), CheckPasswordDialog.TAG);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		webView.saveState(outState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		ActionBar actionBar = activity.getActionBar();
		actionBar.hide();
	}

	
}

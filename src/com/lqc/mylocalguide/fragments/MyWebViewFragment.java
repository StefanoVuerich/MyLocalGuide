package com.lqc.mylocalguide.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class MyWebViewFragment extends Fragment {

	private WebView webView;
	private ImageView adminBtn;
	public final static String _TAG = "MyWebViewFragment";
	private View rootView;
	private SharedPreferences settings;
	private ProgressBar prograssBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.webview_fragment_layout,
				container, false);

		settings = getActivity().getSharedPreferences(
				ConfigurationStorage.getInstance().STORAGE, 0);

		prograssBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		prograssBar.setMax(100);
		setWebView();
		
		if (savedInstanceState != null)
			webView.restoreState(savedInstanceState);
		else 
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

	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView() {
		webView = (WebView)rootView.findViewById(R.id.mWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setInitialScale(settings.getInt(ConfigurationStorage.ZOOM, 100));
		webView.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				setValue(newProgress);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				MyWebViewFragment.this.prograssBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				MyWebViewFragment.this.prograssBar.setVisibility(View.GONE);
			}

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

	private void setValue(int progress) {
		this.prograssBar.setProgress(progress);
	}

	public static MyWebViewFragment get() {
		MyWebViewFragment fr = new MyWebViewFragment();
		return fr;
	}

	private void showCheckPasswordDialog(String flag) {
		CheckPasswordDialog checkPasswordDialog = CheckPasswordDialog.get(flag);
		checkPasswordDialog.show(getFragmentManager(), CheckPasswordDialog.TAG);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (webView != null) {
			webView.saveState(outState);
		}
	}
}

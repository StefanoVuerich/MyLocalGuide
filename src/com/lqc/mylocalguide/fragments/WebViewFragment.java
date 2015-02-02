package com.lqc.mylocalguide.fragments;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.MyRepository;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomControls;

public class WebViewFragment extends Fragment {

	public static final String SHOW_PASSWORD_DIALOG = "SHOW_PASSWORD_DIALOG";
	private WebView webView;
	private ZoomControls zoomControls;
	private Button adminBtn;
	private View rootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//webView = (WebView) rootView.findViewById(R.id.mWebView);
		//webView.setInitialScale(50);
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
		
		/*ZoomDensity setting = webView.getSettings().getDefaultZoom();
		webView.getSettings().setDefaultZoom(ZoomDensity.FAR);*/

		webView.setWebViewClient(new WebViewClient() {
			@Override
	        public void onScaleChanged(WebView view, float oldScale, float newScale) {
	            super.onScaleChanged(view, oldScale, newScale);
	            Log.v("JUJU","chenged");
	        }
			
		});
		
		webView.loadUrl(MyRepository.get().getUrl());
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDisplayZoomControls(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setInitialScale(120);
		
		//webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

		zoomControls = (ZoomControls) rootView.findViewById(R.id.zoomControls1);
		zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
				startActivity(browserIntent);
			}
		});
		zoomControls.setOnZoomInClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "zoom in", Toast.LENGTH_SHORT)
						.show();
				// boolean s = webView.zoomIn();
				float x = webView.getScaleX();
				float y = webView.getScaleY();

				webView.setScaleX((float) (x + 1));
				webView.setScaleY((float) (y + 1));
				
				int contentheight = webView.getContentHeight();
				int height = webView.getHeight();
				int width = webView.getWidth();
				webView.setInitialScale(120);
			}
		});

		adminBtn = (Button) rootView.findViewById(R.id.adminBtn);
		adminBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showCheckPasswordDialog();
			}
		});
		
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

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

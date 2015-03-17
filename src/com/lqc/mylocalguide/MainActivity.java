package com.lqc.mylocalguide;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.lqc.mylocalguide.fragments.AdministrationFragment;
import com.lqc.mylocalguide.fragments.AdministrationFragment.OnActionSelected;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog.ICheckPassword;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment.IExitApplicationConfirm;
import com.lqc.mylocalguide.fragments.MyWebViewFragment;
import com.lqc.mylocalguide.fragments.NoConnectionFragment;
import com.lqc.mylocalguide.login.LoginHandler;
import com.lqc.mylocalguide.storage.ConfigurationStorage;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Shell;

public class MainActivity extends Activity implements ICheckPassword,
		OnActionSelected, IExitApplicationConfirm, OnTouchListener {

	@Override
	protected void onResume() {
		super.onResume();
		hideVirtualButtons();
	}

	@SuppressLint("InlinedApi")
	private void hideVirtualButtons() {
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v("jajaja","touch");
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		Log.v("jajaja","track");
		return super.onTrackballEvent(event);
	}

	@Override
	public void takeKeyEvents(boolean get) {
		Log.v("jajaja","take");
		super.takeKeyEvents(get);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		int x = event.getAction();

		Log.v("jajaja", "code " + x);

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(MainActivity.this, "move home clicked",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 * this.getWindow().setFlags(WindowManager.LayoutParams.,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		setContentView(R.layout.activity_main);

		if (RootShell.isRootAvailable()) {
			Log.v("jajaja", "root is avaiable");

			if (RootShell.exists("build.prop")) {
				Log.v("jajaja", "build.prop found");
			}
			;
		} else {
			Log.v("jajaja", "root is not avaiable");
		}

		try {
			Shell shell = RootShell.getShell(true, 1000,
					Shell.ShellContext.SYSTEM_APP);
			if (shell != null)
				Log.v("jajaja", "we have root shell");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RootDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		checkForInternetConnection(MainActivity.this);
	}

	private void showNoConnectionFragment() {
		getFragmentManager()
				.beginTransaction()
				.add(R.id.fragmentsContainer,
						NoConnectionFragment.getInstance(), null).commit();
	}

	private void initConfigurationStorage() {
		String filename = ConfigurationStorage.STORAGE_FILE_NAME;
		String filePath = Environment.getDataDirectory().getAbsolutePath()
				+ "/data/" + getPackageName() + "/shared_prefs/" + filename;
		File f = new File(filePath);

		if (!f.exists()) {
			ConfigurationStorage.getInstance().init(this);
		}
	}

	private void checkForInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(MainActivity.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		if (!isConnected)
			showNoConnectionFragment();
		else
			showWebViewFragment();
	}

	public void reCheckForConnection() {
		checkForInternetConnection(MainActivity.this);
	}

	@Override
	public void onLogin(String mode_flag, String password) {

		boolean isLoginCorrect = LoginHandler.getInstance().checkLogin(this,
				mode_flag, password);

		if (mode_flag.equals("exit")) {

			if (isLoginCorrect) {

				closeCheckPasswordDialog();
				showConfirmExitDialog();
			} else {
				wrongPassword();
			}

		} else if (mode_flag.equals("admin")) {

			if (isLoginCorrect) {

				closeCheckPasswordDialog();
				showAdministrationFragment();
			} else {
				wrongPassword();
			}
		}
	}

	private void showConfirmExitDialog() {
		ConfirmApplicationExitFragment confirmExitDialog = ConfirmApplicationExitFragment
				.get();
		confirmExitDialog.show(getFragmentManager(),
				ConfirmApplicationExitFragment.getTAG());
	}

	private void wrongPassword() {
		CheckPasswordDialog checkPasswordFragment = (CheckPasswordDialog) getFragmentManager()
				.findFragmentByTag(CheckPasswordDialog.TAG);
		checkPasswordFragment.clearPasswordEditText();
		checkPasswordFragment.shake();
	}

	@Override
	public void onCancel() {
		closeCheckPasswordDialog();

	}

	private void closeCheckPasswordDialog() {
		CheckPasswordDialog checkPasswordDialog = (CheckPasswordDialog) getFragmentManager()
				.findFragmentByTag(CheckPasswordDialog.TAG);
		checkPasswordDialog.dismiss();
	}

	@Override
	public void onSave() {
		showWebViewFragment();
	}

	@Override
	public void onCancelSave() {
		showWebViewFragment();
	}

	private void showWebViewFragment() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentsContainer, MyWebViewFragment.get(),
						MyWebViewFragment._TAG).commit();
	}

	private void showAdministrationFragment() {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentsContainer,
						AdministrationFragment.getInstance(),
						AdministrationFragment._TAG).addToBackStack(null)
				.commit();
	}

	@Override
	public void onExitApplication() {
		System.exit(0);
	}

	@Override
	public void onCancelExit() {
		closeConfirmExitApplicationFragment();
	}

	private void closeConfirmExitApplicationFragment() {
		ConfirmApplicationExitFragment confirmApplicationExitFragment = (ConfirmApplicationExitFragment) getFragmentManager()
				.findFragmentByTag(ConfirmApplicationExitFragment.getTAG());
		confirmApplicationExitFragment.dismiss();
	}

	private void setSystemUIEnabled(boolean enabled) {
		try {
			Process p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("pm " + (enabled ? "enable" : "disable")
					+ " com.android.systemui\n");
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Toast.makeText(MainActivity.this, "touch", Toast.LENGTH_LONG).show();
		return false;
	}
}

package com.lqc.mylocalguide;

import com.lqc.mylocalguide.fragments.AdministrationFragment;
import com.lqc.mylocalguide.fragments.AdministrationFragment.OnActionSelected;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog;
import com.lqc.mylocalguide.fragments.ConfirmApplicationExitFragment.IExitApplicationConfirm;
import com.lqc.mylocalguide.fragments.WebViewFragment;
import com.lqc.mylocalguide.fragments.CheckPasswordDialog.ICheckPassword;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ViewDebug.FlagToString;
import android.widget.Toast;

public class MainActivity extends Activity implements ICheckPassword,
		OnActionSelected, IExitApplicationConfirm {

	public static final String WEB_VIEW_FRAGMENT = "WEB_VIEW_FRAGMENT";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		showWebViewFragment();
	}

	@Override
	public void onLogin(String mode_flag, String password) {

		if (mode_flag.equals("exit")) 
		{
			if (password != null && (password.equals(MyRepository.get().getADMIN_PASSWORD()) || password.equals(MyRepository.get().getUSER_PASSWORD()))) 
			{
				Toast.makeText(MainActivity.this,
						"RIGHT PASSWORD Going to exit", Toast.LENGTH_SHORT)
						.show();
				closeCheckPasswordDialog();
				onExitApplication();
			} 

			
		} else if (mode_flag.equals("admin")) {
			if (password != null && password.equals(MyRepository.get().getADMIN_PASSWORD())) {
				Toast.makeText(MainActivity.this,
						"RIGHT PASSWORD Going to admin", Toast.LENGTH_SHORT)
						.show();
				closeCheckPasswordDialog();
				Fragment fr = new AdministrationFragment();
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragmentsContainer, fr);
				ft.commit();
			}
		}
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
	public void OnSave() {
		showWebViewFragment();
	}

	@Override
	public void OnCancel() {
		showWebViewFragment();
	}

	public void showWebViewFragment() {
		Fragment fragment = WebViewFragment.get();

		getFragmentManager().beginTransaction()
				.add(R.id.fragmentsContainer, fragment, WEB_VIEW_FRAGMENT)
				.commit();
	}

	@Override
	public void onExitApplication() {
		System.exit(0);
	}

	@Override
	public void onCancelExit() {
		// Do nothing
	}
}

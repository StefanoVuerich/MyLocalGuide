package com.lqc.mylocalguide.login;

import android.app.Activity;
import android.widget.TextView;

import com.lqc.mylocalguide.R;
import com.lqc.mylocalguide.storage.ConfigurationStorage;

public class PasswordHandler {

	private static final String ERROR_FEEDBACK = "Password and confirm password must match";
	private static PasswordHandler instance;

	private PasswordHandler() {
	}

	public static PasswordHandler getInstance() {
		if (instance == null) {
			instance = new PasswordHandler();
		}
		return instance;
	}

	/*public boolean changeAdminPassword(Fragment fragment, Context context,
			String password, String confirmPassword) {
		if (arePasswordEquals(password, confirmPassword)) {
			ConfigurationStorage.getInstance().updateAdminPassword(context,
					password);
			return true;
		} else {
			triggerPasswordNotMatchError(0, fragment);
			return false;
		}
	}*/
	
	public boolean changeAdminPassword(Activity activity,
			String password, String confirmPassword) {
		if (arePasswordEquals(password, confirmPassword)) {
			ConfigurationStorage.getInstance().updateAdminPassword(activity,
					password);
			return true;
		} else {
			triggerPasswordNotMatchError(0, activity);
			return false;
		}
	}

	public boolean changeUserPassword(Activity activity,
			String password, String confirmPassword) {
		if (arePasswordEquals(password, confirmPassword)) {
			ConfigurationStorage.getInstance().updateUserPassword(activity,
					password);
			return true;
		} else {
			triggerPasswordNotMatchError(1, activity);
			return false;
		}
	}

	// 0 is admin, 1 is user
	private void triggerPasswordNotMatchError(int who, Activity activity) {
		
		int feedbackTxtRef = 0;
		if(who == 0)
			feedbackTxtRef = R.id.newAdminPasswordFeedback;
		else if(who == 1) 
			feedbackTxtRef = R.id.newUserPasswordFeedback;
		
		TextView feedbackTxt = (TextView) activity.findViewById(feedbackTxtRef);
		feedbackTxt.setText("");
		feedbackTxt.setText(ERROR_FEEDBACK);
	}

	private boolean arePasswordEquals(String password, String conformPassword) {
		if (password.equals(conformPassword))
			return true;
		return false;
	}
}

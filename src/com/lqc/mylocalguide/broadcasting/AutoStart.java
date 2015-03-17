package com.lqc.mylocalguide.broadcasting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lqc.mylocalguide.MainActivity;

public class AutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			Intent i = new Intent(context, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}

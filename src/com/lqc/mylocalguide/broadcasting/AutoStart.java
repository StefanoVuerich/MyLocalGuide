package com.lqc.mylocalguide.broadcasting;

import com.lqc.mylocalguide.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PatternMatcher;
import android.util.Log;
import android.widget.Toast;

public class AutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("jaja", intent.toString());
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			Intent i = new Intent(context, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			
			/*IntentFilter filter = new IntentFilter(Intent.ACTION_VIEW);
			filter.addDataScheme("http");
			filter.addDataAuthority("www.example.com", null);
			filter.addDataScheme("http");
			filter.addDataPath("/ciao", PatternMatcher.PATTERN_PREFIX);*/
			
		}
		
		if(intent.getAction().equals(Intent.ACTION_VIEW)) {
			/*Intent i = new Intent(context, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();*/
			
		}
	}
}

package com.lqc.mylocalguide.utilities;

import java.util.ArrayList;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqc.mylocalguide.R;

public class WifiListAdapter extends BaseAdapter{

	ArrayList<ScanResult> mArray;
	Context context;
	
	public WifiListAdapter(Context context, ArrayList<ScanResult> data) {
		this.mArray = data;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArray.size();
	}

	@Override
	public ScanResult getItem(int position) {
		return mArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		//not the best way...to be fixed
		ScanResult item = getItem(position);
		return item.timestamp;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vView;
		
		if(convertView != null) 
		{
			vView = convertView;
		}
		else
		{
			LayoutInflater vInflater =  LayoutInflater.from(context);
			vView = vInflater.inflate(R.layout.wifi_connection_item_layout, null);
			ViewHolder holder = new ViewHolder();
			holder.wifiName = (TextView)vView.findViewById(R.id.wifiName);
			holder.wifiSignal = (TextView) vView.findViewById(R.id.wifiSignal);
			holder.wifiImage = (ImageView)vView.findViewById(R.id.wifiImage);
			
			vView.setTag(holder);
		}
		
		ScanResult vItem = getItem(position);
		ViewHolder holder = (ViewHolder) vView.getTag();
		holder.wifiName.setText("Wifi Connection: " + vItem.SSID);
		holder.wifiSignal.setText("Signal Level: " +  vItem.level);
		holder.wifiImage.setImageResource(R.drawable.lqc_logo_lo);
		
		return vView;
	}

	private class ViewHolder {
		private TextView wifiName, wifiSignal;
		private ImageView wifiImage;
	}
}

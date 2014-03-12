package com.neusoft.nees.common;

import com.neusoft.nees.widget.MainActivity;
import com.neusoft.nees.widget.SignActivity;
import com.neusoft.nees.widget.TakePhotoActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	static final String ACTION="android.intent.action.BOOT_COMPLETED"; 
	static final String SIGN_ACTION="nees.signName"; 
	static final String TAKE_PHOTO="nees.takePhoto";
	static final String SUGGEST_PROD="nees.suggestProd";
	static final String SUGGEST_ACTIVE="nees.suggestActive";
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION)){
			Intent bootStart=new Intent(context,MainActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		} 
		
		if(intent.getAction().equals(SIGN_ACTION)){
			Intent bootStart=new Intent(context,SignActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
		
		if(intent.getAction().equals(TAKE_PHOTO)){
			Intent bootStart=new Intent(context,TakePhotoActivity.class);
			bootStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootStart);
		}
	}
}

















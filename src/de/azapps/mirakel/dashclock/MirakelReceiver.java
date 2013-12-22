package de.azapps.mirakel.dashclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MirakelReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		MirakelExtension.updateWidget();
	}

}

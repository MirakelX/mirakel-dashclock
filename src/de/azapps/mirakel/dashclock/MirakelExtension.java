package de.azapps.mirakel.dashclock;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class MirakelExtension extends DashClockExtension {

//	private static final String TAG = "MirakelExtension";
	public static final CharSequence PREF_NAME = "pref";

	@Override
	protected void onUpdateData(int reason) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		int list_id = Integer.parseInt(prefs.getString("startupList", "-1"));
		String[] col = { "name,priority,due" };
		//TODO implement where from speciallist
		Cursor c = getContentResolver()
				.query(Uri.parse("content://de.azapps.mirakel.provider/tasks"),
						col,
						"list_id=" + list_id + " and done=0",
						null,
						"priority desc, case when (due is NULL) then date('now','+1000 years') else date(due) end asc");
		c.moveToFirst();
		String status="";
		if(c.getCount()==0)
			status=getString(R.string.status0);
		else if(c.getCount()==1)
			status=getString(R.string.status1);
		else
			status=c.getCount()+" "+getString(R.string.status2);
		String expBody="";
		if(c.getCount()>0){
			SimpleDateFormat in=new SimpleDateFormat(getString(R.string.due_dbformat));
			SimpleDateFormat out=new SimpleDateFormat(getString(R.string.due_outformat));
			Date t=null;
			try {
				t=in.parse(c.getString(2));
			} catch (Exception e) {
			}
			expBody=c.getString(0)+(t==null?" ":" "+getString(R.string.to)+" "+out.format(t));
		}
		
		publishUpdate(new ExtensionData()
				.visible(true)
				.icon(R.drawable.ic_launcher)
				.status(status)
				.expandedBody(expBody));

	}
}


package de.azapps.mirakel.dashclock;

import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class MirakelSettingsActivity extends PreferenceActivity {
//	private static final String TAG = "MirakelSettingsActivity";

	@SuppressWarnings("deprecation")//TODO Why?
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_launcher);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.pref_xml);
		ListPreference startupListPreference =  (ListPreference) findPreference("startupList");
		String[] s={"_id","name"};
		//Get Lists from Mirakel-Contentresolver
		Cursor c = getContentResolver().query(
				Uri.parse("content://de.azapps.mirakel.provider/special_lists"),s ,
				"1=1", null, null);
		List<CharSequence>values=new ArrayList<CharSequence>();
		List<CharSequence>entries=new ArrayList<CharSequence>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			values.add(""+(-1*c.getInt(0)));
			entries.add(c.getString(1));
			c.moveToNext();
		}
		c = getContentResolver().query(
				Uri.parse("content://de.azapps.mirakel.provider/lists"),s ,
				"1=1", null, "lft");
		c.moveToFirst();
		while(!c.isAfterLast()){
			values.add(c.getString(0));
			entries.add(c.getString(1));
			c.moveToNext();
		}		
		c.close();
		startupListPreference.setEntries(entries.toArray(new String[entries.size()]));
		startupListPreference.setEntryValues(values.toArray(new String[values.size()]));
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

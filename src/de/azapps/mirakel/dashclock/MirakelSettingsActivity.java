/*******************************************************************************
 * Mirakel is an Android App for managing your ToDo-Lists
 * 
 * Copyright (c) 2013 Anatolij Zelenin, Georg Semmler.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.azapps.mirakel.dashclock;

import java.util.ArrayList;
import java.util.List;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class MirakelSettingsActivity extends PreferenceActivity {
	private static final String TAG = "MirakelSettingsActivity";
	private NumberPicker numberPicker;

	@SuppressWarnings("deprecation")
	// TODO Why?
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Fix Layout
		Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
		d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
		getActionBar().setIcon(d);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.rgb(0, 153, 204)));
		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");
		((TextView) findViewById(titleId)).setTextColor(Color.WHITE);
		getListView().setDividerHeight(0);
		addPreferencesFromResource(R.xml.pref_xml);
		ListPreference startupListPreference = (ListPreference) findPreference("startupList");
		String[] s = { "_id", "name" };
		// Get Lists from Mirakel-Contentresolver
		Cursor c=null;
		try {
			c= getContentResolver()
				.query(Uri
						.parse("content://de.azapps.mirakel.provider/special_lists"),
						s, "1=1", null, null);
		}catch(Exception e){
			Log.e(TAG,"Cannot communicate to Mirakel");
			return;
		}
		if (c == null) {
			Log.wtf(TAG, "Mirakel-Contentprovider not Found");
			Toast.makeText(this, getString(R.string.installMirakel),
					Toast.LENGTH_SHORT).show();
			return;
		}
		List<CharSequence> values = new ArrayList<CharSequence>();
		List<CharSequence> entries = new ArrayList<CharSequence>();
		c.moveToFirst();
		while (!c.isAfterLast()) {
			values.add("" + (-1 * c.getInt(0)));
			entries.add(c.getString(1));
			c.moveToNext();
		}
		c = getContentResolver().query(
				Uri.parse("content://de.azapps.mirakel.provider/lists"), s,
				"1=1", null, "lft");
		c.moveToFirst();
		while (!c.isAfterLast()) {
			values.add(c.getString(0));
			entries.add(c.getString(1));
			c.moveToNext();
		}
		c.close();
		startupListPreference.setEntries(entries.toArray(new String[entries
				.size()]));
		startupListPreference.setEntryValues(values.toArray(new String[values
				.size()]));
		Preference p=findPreference("showTasks");
		final Context ctx=this;
		final SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		p.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				numberPicker = new NumberPicker(ctx);
				numberPicker.setMaxValue(5);
				numberPicker.setMinValue(0);
				numberPicker.setWrapSelectorWheel(false);
				numberPicker.setValue(settings.getInt("showTaskNumber", 0));
				numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
				new AlertDialog.Builder(ctx)
				.setTitle(getString(R.string.number_of))
				.setMessage(getString(R.string.how_many))
				.setView(numberPicker)
				.setPositiveButton(getString(R.string.OK),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								SharedPreferences.Editor editor = settings.edit();
								editor.putInt("showTaskNumber", numberPicker.getValue());
								editor.commit();
							}

						})
				.setNegativeButton(getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Do nothing.
							}
						}).show();
				return true;
			}
		});
		

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

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class MirakelExtension extends DashClockExtension {

	private static final String TAG = "MirakelExtension";

	@Override
	protected void onUpdateData(int reason) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		// Get values from Settings
		int list_id = Integer.parseInt(prefs.getString("startupList", "-1"));
		int maxTasks = Integer.parseInt(prefs.getString("showTasks", "1"));
		// Get where-clause
		String where = "";
		if (list_id < 0) {
			String[] col = { "whereQuery" };
			Cursor c = getContentResolver()
					.query(Uri
							.parse("content://de.azapps.mirakel.provider/special_lists"),
							col, " _id=" + (-1 * list_id), null, null);
			if (c == null) {
				Log.wtf(TAG, "Mirakel-Contentprovider not Found");
				Toast.makeText(this, getString(R.string.installMirakel),
						Toast.LENGTH_SHORT).show();
				return;
			}
			c.moveToFirst();
			if (c.getCount() > 0) {
				where = c.getString(0);
			}
			c.close();
		}
		// Get Tasks
		String[] col = { "name,priority,due" };
		Cursor c = getContentResolver()
				.query(Uri.parse("content://de.azapps.mirakel.provider/tasks"),
						col,
						(where == "" ? " list_id=" + list_id : where)
								+ " and done=0 ",
						null,
						"priority desc, case when (due is NULL) then date('now','+1000 years') else date(due) end asc");
		c.moveToFirst();
		// Set Status
		String status = "";
		if (c.getCount() == 0)
			status = getString(R.string.status0);
		else if (c.getCount() == 1)
			status = getString(R.string.status1);
		else
			status = c.getCount() + " " + getString(R.string.status2);
		// Set Body
		String expBody = "";
		if (c.getCount() > 0) {
			SimpleDateFormat in = new SimpleDateFormat(
					getString(R.string.due_dbformat), Locale.US);
			SimpleDateFormat out = new SimpleDateFormat(
					getString(R.string.due_outformat), Locale.getDefault());
			int counter = 0;
			while (!c.isAfterLast() && counter < maxTasks) {
				Date t = null;
				try {
					t = in.parse(c.getString(2));
				} catch (ParseException e) {
					Log.wtf(TAG, "failed to parse Date from db");
				} catch (NullPointerException e) {
				}
				expBody += c.getString(0)
						+ (t == null ? " " : " " + getString(R.string.to) + " "
								+ out.format(t));
				c.moveToNext();
				expBody += (counter < maxTasks - 1 && !c.isAfterLast() ? "\n"
						: "");
				++counter;
			}
		}
		// Set Content
		publishUpdate(new ExtensionData().visible(true)
				.icon(R.drawable.ic_launcher).status(status)
				.expandedBody(expBody));

	}
}

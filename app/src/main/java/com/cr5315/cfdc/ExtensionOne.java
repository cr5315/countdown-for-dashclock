/*
 * Countdown for DashClock Extension
 * Copyright (c) 2014 Benjamin Butzow
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package com.cr5315.cfdc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ExtensionOne extends DashClockExtension {
    private SharedPreferences sharedPreferences;

    private String countdownNumber = "one";

    @Override
    protected void onInitialize(boolean isReconnect) {
        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int reason) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Countdown countdown = new Countdown(this, countdownNumber);
        int[] diffs = countdown.getTimeRemaining();

        ExtensionData extensionData = new ExtensionData();

        extensionData.status(countdown.getStatus(diffs));
        extensionData.expandedTitle(sharedPreferences.getString("pref_title_" + countdownNumber, getString(R.string.no_countdown_title)));
        extensionData.expandedBody(countdown.getBody(diffs));
        extensionData.icon(sharedPreferences.getInt("pref_icon_" + countdownNumber, R.drawable.ic_launcher));
        extensionData.clickIntent(getClickIntent());
        extensionData.visible(true);

        publishUpdate(extensionData);
    }

    protected Intent getClickIntent() {
        String pref = sharedPreferences.getString("pref_action_" + countdownNumber, null);
        // Check to make sure the pref is set
        // If not, open settings
        if (pref == null) return getSettingsIntent();

        String[] values = getApplicationContext().getResources().getStringArray(R.array.click_actions_values);
        if (pref.matches(values[0])) {
            // Update Countdown
            return null;
        } else if (pref.matches(values[1])) {
            // Open calendar
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());

            int year = sharedPreferences.getInt("pref_date_year_" + countdownNumber, cal.get(Calendar.YEAR));
            int month = sharedPreferences.getInt("pref_date_month_" + countdownNumber, cal.get(Calendar.MONTH));
            int day = sharedPreferences.getInt("pref_date_day_" + countdownNumber, cal.get(Calendar.DAY_OF_MONTH));

            cal.set(year, month, day);
            long time = cal.getTime().getTime();
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            builder.appendPath(Long.toString(time));

            return new Intent(Intent.ACTION_VIEW, builder.build());
        } else if (pref.matches(values[2])) {
            // Open settings
            return getSettingsIntent();
        }  else {
            return getSettingsIntent();
        }
    }

    private Intent getSettingsIntent() {
        Intent settingsIntent = new Intent(getApplicationContext(), ExtensionOneSettings.class);
        settingsIntent.putExtra("fromExtension", true);
        return settingsIntent;
    }
}
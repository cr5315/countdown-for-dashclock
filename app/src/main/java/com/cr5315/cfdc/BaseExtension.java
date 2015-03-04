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

public abstract class BaseExtension extends DashClockExtension {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onInitialize(boolean isReconnect) {
        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int reason) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Countdown countdown = new Countdown(this, getCountdownNumber());
        int[] diffs = countdown.getTimeRemaining();

        ExtensionData extensionData = new ExtensionData();

        extensionData.status(countdown.getStatus(diffs));
        extensionData.expandedTitle(sharedPreferences.getString("pref_title_" + getCountdownNumber(), getString(R.string.no_countdown_title)));
        extensionData.expandedBody(countdown.getBody(diffs));
        extensionData.icon(sharedPreferences.getInt("pref_icon_" + getCountdownNumber(), R.drawable.ic_launcher));
        extensionData.clickIntent(getClickIntent());
        extensionData.visible(true);

        publishUpdate(extensionData);
    }

    protected Intent getClickIntent() {
        String pref = sharedPreferences.getString("pref_action_" + getCountdownNumber(), null);
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

            int year = sharedPreferences.getInt("pref_date_year_" + getCountdownNumber(), cal.get(Calendar.YEAR));
            int month = sharedPreferences.getInt("pref_date_month_" + getCountdownNumber(), cal.get(Calendar.MONTH));
            int day = sharedPreferences.getInt("pref_date_day_" + getCountdownNumber(), cal.get(Calendar.DAY_OF_MONTH));

            cal.set(year, month, day);
            long time = cal.getTime().getTime();
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            builder.appendPath(Long.toString(time));

            return new Intent(Intent.ACTION_VIEW, builder.build());
        } else if (pref.matches(values[2])) {
            // Open settings
            return getSettingsIntent();
        } else {
            return getSettingsIntent();
        }
    }

    private Intent getSettingsIntent() {
        Intent settingsIntent = new Intent(getApplicationContext(), getSettingsClass());
        settingsIntent.putExtra("fromExtension", true);
        return settingsIntent;
    }

    public abstract Class getSettingsClass();

    public abstract String getCountdownNumber();
}

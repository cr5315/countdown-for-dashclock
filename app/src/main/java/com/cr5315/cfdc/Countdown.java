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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Locale;

public class Countdown {
    private Context context;
    private SharedPreferences sharedPreferences;

    private String PREF_TITLE = "pref_title_";
    private String PREF_CUSTOM_MESSAGE = "pref_custom_message_";
    private String PREF_MESSAGE = "pref_message_";
    private String PREF_DAYS_ONLY = "pref_days_only_";
    private String PREF_DATE_DAY = "pref_date_day_";
    private String PREF_DATE_MONTH = "pref_date_month_";
    private String PREF_DATE_YEAR = "pref_date_year_";
    private String PREF_TIME_HOUR = "pref_time_hour_";
    private String PREF_TIME_MINUTE = "pref_time_minute_";
    private String PREF_INVERT = "pref_invert_";

    public static final int DIFF_DAYS = 0;
    public static final int DIFF_HOURS = 1;
    public static final int DIFF_MINUTES = 2;

    private boolean isDaysOnly = false;
    private boolean isPast = false;

    public Countdown(Context context, String number) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        PREF_TITLE += number;
        PREF_CUSTOM_MESSAGE += number;
        PREF_MESSAGE += number;
        PREF_DAYS_ONLY += number;
        PREF_DATE_DAY += number;
        PREF_DATE_MONTH += number;
        PREF_DATE_YEAR += number;
        PREF_TIME_HOUR += number;
        PREF_TIME_MINUTE += number;
        PREF_INVERT += number;
    }

    public int[] getTimeRemaining() {
        Calendar now = Calendar.getInstance();
        Calendar event = Calendar.getInstance();

        // Set the event calendar
        int year = sharedPreferences.getInt(PREF_DATE_YEAR, event.get(Calendar.YEAR));
        int month = sharedPreferences.getInt(PREF_DATE_MONTH, event.get(Calendar.MONTH));
        int day = sharedPreferences.getInt(PREF_DATE_DAY, event.get(Calendar.DAY_OF_MONTH));
        int hour = sharedPreferences.getInt(PREF_TIME_HOUR, event.get(Calendar.HOUR_OF_DAY));
        int minute = sharedPreferences.getInt(PREF_TIME_MINUTE, event.get(Calendar.MINUTE));

        isDaysOnly = sharedPreferences.getBoolean(PREF_DAYS_ONLY, false);
        if (isDaysOnly)
            event.set(year, month, day, 0, 0, 0);
        else
            event.set(year, month, day, hour, minute, 0);

        long nowInMillis = now.getTimeInMillis();
        long eventInMillis = event.getTimeInMillis();
        long diff = eventInMillis - nowInMillis;

        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        int hours = (int) (diffHours - (diffDays * 24));
        int minutes = (int) (diffMinutes - (diffHours * 60)) + 1;

        // If it's a days only countdown, add a day
        if (isDaysOnly)
            diffDays++;

        // If minutes >= 60, add an hour and remove 60 minutes
        while (minutes >= 60) {
            hours++;
            minutes -= 60;
        }
        if (minutes == 1 && now.get(Calendar.MINUTE) == event.get(Calendar.MINUTE))
            minutes = 0;

        // If hours >= 24, add a day and remove 24 hours
        if (diffDays > 4) hours++; // For some reason if days > 4, hours is short one
        while (hours >= 24) {
            diffDays++;
            hours -= 24;
        }

        if ((minutes < 0 || hours < 0 || diffDays < 0)
                && !sharedPreferences.getBoolean(PREF_CUSTOM_MESSAGE, false)) {
            // It's in the past. If we're using the default message, set isPast
            isPast = true;
            diffDays *= -1;
            hours *= -1;
            minutes *= -1;
        }

        if (sharedPreferences.getBoolean(PREF_CUSTOM_MESSAGE, false)
                && sharedPreferences.getBoolean(PREF_INVERT, false)) {
            diffDays *= -1;
            hours *= -1;
            minutes *= -1;
        }

        return new int[] { (int) diffDays, hours, minutes };
    }

    public String getStatus(int[] diffs) {
        if (isDaysOnly) {
            if (diffs[DIFF_DAYS] == 0) {
                return context.getString(R.string.today);
            } else {
                return String.format(Locale.getDefault(),
                        context.getString(R.string.short_title_days_only), diffs[DIFF_DAYS]);
            }
        } else {
            if (diffs[DIFF_DAYS] > 0) {
                return String.format(Locale.getDefault(),
                        context.getString(R.string.short_title_days), diffs[DIFF_DAYS], diffs[DIFF_HOURS]);
            } else if (diffs[DIFF_HOURS] > 0) {
                return String.format(Locale.getDefault(),
                        context.getString(R.string.short_title_hours), diffs[DIFF_HOURS], diffs[DIFF_MINUTES]);
            } else if (diffs[DIFF_MINUTES] > 0) {
                return String.format(Locale.getDefault(),
                        context.getString(R.string.short_title_minutes), diffs[DIFF_MINUTES]);
            } else {
                return context.getString(R.string.now);
            }
        }
    }

    public String getBody(int[] diffs) {
        String timeRemaining = context.getString(R.string.no_countdown_body);

        if (isDaysOnly) {
            if (diffs[DIFF_DAYS] == 0) {
                timeRemaining = context.getString(R.string.today);
            } else {
                String days = context.getResources().getQuantityString(R.plurals.days, diffs[DIFF_DAYS], diffs[DIFF_DAYS]);

                timeRemaining = context.getString(R.string.full_countdown_days_only, days);
            }

        } else {
            if (diffs[DIFF_DAYS] != 0) {
                String days = context.getResources().getQuantityString(R.plurals.days, diffs[DIFF_DAYS], diffs[DIFF_DAYS]);
                String hours = context.getResources().getQuantityString(R.plurals.hours, diffs[DIFF_HOURS], diffs[DIFF_HOURS]);
                String minutes = context.getResources().getQuantityString(R.plurals.minutes, diffs[DIFF_MINUTES], diffs[DIFF_MINUTES]);

                timeRemaining = context.getString(R.string.full_countdown_days, days, hours, minutes);
            } else if (diffs[DIFF_HOURS] != 0) {
                String hours = context.getResources().getQuantityString(R.plurals.hours, diffs[DIFF_HOURS], diffs[DIFF_HOURS]);
                String minutes = context.getResources().getQuantityString(R.plurals.minutes, diffs[DIFF_MINUTES], diffs[DIFF_MINUTES]);

                timeRemaining = context.getString(R.string.full_countdown_hours, hours, minutes);
            } else if (diffs[DIFF_MINUTES] != 0) {
                String minutes = context.getResources().getQuantityString(R.plurals.minutes, diffs[DIFF_MINUTES], diffs[DIFF_MINUTES]);

                timeRemaining = context.getString(R.string.full_countdown_minutes, minutes);
            }
        }

        String title = sharedPreferences.getString(PREF_TITLE, null);
        if (!sharedPreferences.getBoolean(PREF_CUSTOM_MESSAGE, false)) {
            if (isDaysOnly && diffs[DIFF_DAYS] == 0) {
                return context.getString(R.string.today_extended, title);
            } else if (diffs[DIFF_DAYS] == 0 && diffs[DIFF_HOURS] == 0 && diffs[DIFF_MINUTES] == 0) {
                return context.getString(R.string.now_extended, title);
            } else if (isPast) {
                // isPast is true, so we're using the default message
                return context.getString(R.string.since, timeRemaining, title);
            } else {
                return context.getString(R.string.until, timeRemaining, title);
            }
        } else {
            String message = sharedPreferences.getString(PREF_MESSAGE, context.getString(R.string.message_prompt));

            message = message.replace("%title", title);
            message = message.replace("%time", timeRemaining);

            return message;
        }
    }
}
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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class ExtensionThreeSettings extends PreferenceActivity {
    private Context context;
    private Preference title, message, icon, date, time, action;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    private final String PREF_TITLE = "pref_title_three";
    private final String PREF_MESSAGE = "pref_message_three";
    private final String PREF_ICON = "pref_icon_three";
    private final String PREF_DATE = "pref_date_three";
    private static final String PREF_DATE_DAY = "pref_date_day_three";
    private static final String PREF_DATE_MONTH = "pref_date_month_three";
    private static final String PREF_DATE_YEAR = "pref_date_year_three";
    private final String PREF_TIME = "pref_time_three";
    private static final String PREF_TIME_HOUR = "pref_time_hour_three";
    private static final String PREF_TIME_MINUTE = "pref_time_minute_three";
    private final String PREF_ACTION = "pref_action_three";

    private boolean showUpdateNotice = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            showUpdateNotice = extras.getBoolean("fromExtension");
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_three);

        title           = findPreference(PREF_TITLE);
        message         = findPreference(PREF_MESSAGE);
        icon            = findPreference(PREF_ICON);
        date            = findPreference(PREF_DATE);
        time            = findPreference(PREF_TIME);
        action          = findPreference(PREF_ACTION);

        updateAllSummaries();

        date.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getFragmentManager(), "datePicker");
                return true;
            }
        });

        time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getFragmentManager(), "timePicker");
                return true;
            }
        });

        icon.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showIconPickerDialog();
                return true;
            }
        });

        message.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showMessageDialog();
                return true;
            }
        });

        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.i("CFDC", "pref " + key + " changed");
                if (key.matches(PREF_TITLE)) {
                    updateTitleSummary();
                } else if (key.matches(PREF_MESSAGE)) {
                    updateMessageSummary();
                } else if (key.matches(PREF_ICON)) {
                    updateIconSummary();
                } else if (key.matches(PREF_DATE_YEAR) || key.matches(PREF_DATE_MONTH)
                        || key.matches(PREF_DATE_DAY)) {
                    updateDateSummary();
                } else if (key.matches(PREF_TIME_HOUR) || key.matches(PREF_TIME_MINUTE)) {
                    updateTimeSummary();
                } else if (key.matches(PREF_ACTION)) {
                    updateActionSummary();
                }
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        if (showUpdateNotice)
            Toast.makeText(context, getString(R.string.notice_settings_applied), Toast.LENGTH_LONG).show();
        super.onPause();
    }

    private void updateAllSummaries() {
        updateTitleSummary();
        updateMessageSummary();
        updateIconSummary();
        updateDateSummary();
        updateTimeSummary();
        updateActionSummary();
    }

    private void updateTitleSummary() {
        title.setSummary(sharedPreferences.getString(PREF_TITLE, ""));
    }

    private void updateMessageSummary() {
        message.setSummary(sharedPreferences.getString(PREF_MESSAGE, getString(R.string.pref_message_default)));
    }

    private void updateIconSummary() {
        int i = sharedPreferences.getInt(PREF_ICON, -1);
        if (i != -1)
            icon.setIcon(i); // i is the resource id for the selected icon
    }

    private void updateDateSummary() {
        int year = sharedPreferences.getInt(PREF_DATE_YEAR, 0);
        int month = sharedPreferences.getInt(PREF_DATE_MONTH, -1);
        int day = sharedPreferences.getInt(PREF_DATE_DAY, 0);


        if (year != 0 && month != 0 && day != 0) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date.setSummary(simpleDateFormat.format(c.getTime()));
        }
    }

    private void updateTimeSummary() {
        int hour = sharedPreferences.getInt(PREF_TIME_HOUR, -1);
        int minute = sharedPreferences.getInt(PREF_TIME_MINUTE, -1);

        if (hour > -1 && minute > -1) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");;
            time.setSummary(simpleDateFormat.format(c.getTime()));
        }
    }

    private void updateActionSummary() {
        String selection = sharedPreferences.getString(PREF_ACTION, null);

        if (selection != null) {
            ArrayList<String> values = new ArrayList<String>();
            values.addAll(Arrays.asList(getResources().getStringArray(R.array.click_actions_values)));

            int position = values.indexOf(selection);
            if (position == -1) position = 0;

            String[] fancyValues = getResources().getStringArray(R.array.click_actions);

            action.setSummary(fancyValues[position]);
        }
    }

    private void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.pref_message);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_message, null);
        final EditText editText = (EditText) view.findViewById(R.id.editText);
        String pastPref = sharedPreferences.getString(PREF_MESSAGE, getString(R.string.pref_message_default));
        editText.setText(pastPref);
        editText.setSelection(pastPref.length());

        Button title = (Button) view.findViewById(R.id.title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(editText.getText() + "%title");
                editText.setSelection(editText.getText().length());
            }
        });

        Button time = (Button) view.findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(editText.getText() + "%time");
                editText.setSelection(editText.getText().length());
            }
        });


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putString(PREF_MESSAGE, editText.getText().toString());
                editor.commit();
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void showIconPickerDialog() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.pref_icon);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_icon_picker, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new IconPickerAdapter(context));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                editor.putInt(PREF_ICON, IconPickerAdapter.thumbs[position]);
                editor.commit();
                Log.i("CFDC", "Icon id: " + IconPickerAdapter.thumbs[position]);
                Toast.makeText(context, R.string.icon_set, Toast.LENGTH_LONG).show();
            }

        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        builder.create();
        builder.show();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());


            int hour, minute;
            final Calendar c = Calendar.getInstance();
            // If no date has been chosen, use the current date
            if (prefs.getInt(PREF_TIME_HOUR, -5) == -5) {
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            } else {
                hour = prefs.getInt(PREF_TIME_HOUR, c.get(Calendar.HOUR_OF_DAY));
                minute = prefs.getInt(PREF_TIME_MINUTE, c.get(Calendar.MINUTE));
            }

            // Create a new instance of TimePickerDialog
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            editor = prefs.edit();

            Log.i("CfDC", "Hour: " + hourOfDay + " Minute: " + minute);

            editor.putInt(PREF_TIME_HOUR, hourOfDay);
            editor.putInt(PREF_TIME_MINUTE, minute);
            editor.commit();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int year, month, day;
            Calendar c = Calendar.getInstance();
            // If no time has been chosen, use the current time
            if (prefs.getInt(PREF_DATE_YEAR, -5) == -5) {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                year = prefs.getInt(PREF_DATE_YEAR, c.get(Calendar.YEAR));
                month = prefs.getInt(PREF_DATE_MONTH, c.get(Calendar.MONTH));
                day = prefs.getInt(PREF_DATE_DAY, c.get(Calendar.DAY_OF_MONTH));
            }

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            editor = prefs.edit();

            Log.i("CfDC", "Year: " + year + " Month: " + monthOfYear + " Day: " + dayOfMonth);

            editor.putInt(PREF_DATE_YEAR, year);
            editor.putInt(PREF_DATE_MONTH, monthOfYear);
            editor.putInt(PREF_DATE_DAY, dayOfMonth);
            editor.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
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

public abstract class BaseExtensionSettings extends PreferenceActivity {

    private Context context;
    private Preference title, message, icon, date, time, action;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String PREF_TITLE = "pref_title_";
    private static final String PREF_MESSAGE = "pref_message_";
    private static final String PREF_ICON = "pref_icon_";
    private static final String PREF_DATE = "pref_date_";
    private static final String PREF_DATE_DAY = "pref_date_day_";
    private static final String PREF_DATE_MONTH = "pref_date_month_";
    private static final String PREF_DATE_YEAR = "pref_date_year_";
    private static final String PREF_TIME = "pref_time_";
    private static final String PREF_TIME_HOUR = "pref_time_hour_";
    private static final String PREF_TIME_MINUTE = "pref_time_minute_";
    private static final String PREF_ACTION = "pref_action_";

    private boolean showUpdateNotice = false;

    public String getPrefTitle() {
        return PREF_TITLE + getCountdownNumber();
    }

    public String getPrefMessage() {
        return PREF_MESSAGE + getCountdownNumber();
    }

    public String getPrefIcon() {
        return PREF_ICON + getCountdownNumber();
    }

    public String getPrefDate() {
        return PREF_DATE + getCountdownNumber();
    }

    public String getPrefDateDay() {
        return PREF_DATE_DAY + getCountdownNumber();
    }

    public String getPrefDateMonth() {
        return PREF_DATE_MONTH + getCountdownNumber();
    }

    public String getPrefDateYear() {
        return PREF_DATE_YEAR + getCountdownNumber();
    }

    public String getPrefTime() {
        return PREF_TIME + getCountdownNumber();
    }

    public String getPrefTimeHour() {
        return PREF_TIME_HOUR + getCountdownNumber();
    }

    public String getPrefTimeMinute() {
        return PREF_TIME_MINUTE + getCountdownNumber();
    }

    public String getPrefAction() {
        return PREF_ACTION;
    }

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

    public abstract String getCountdownNumber();

    public abstract int getXmlResource();

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(getXmlResource());

        title = findPreference(getPrefTitle());
        message = findPreference(getPrefMessage());
        icon = findPreference(getPrefIcon());
        date = findPreference(getPrefDate());
        time = findPreference(getPrefTime());
        action = findPreference(getPrefAction());

        updateAllSummaries();

        date.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DatePickerFragment dateFragment = new DatePickerFragment();
                dateFragment.setBaseExtension(BaseExtensionSettings.this);
                dateFragment.show(getFragmentManager(), "datePicker");
                return true;
            }
        });

        time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TimePickerFragment timeFragment = new TimePickerFragment();
                timeFragment.setBaseExtension(BaseExtensionSettings.this);
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

        final SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.i("CFDC", "pref " + key + " changed");
                if (key.matches(getPrefTitle())) {
                    updateTitleSummary();
                } else if (key.matches(getPrefMessage())) {
                    updateMessageSummary();
                } else if (key.matches(getPrefIcon())) {
                    updateIconSummary();
                } else if (key.matches(getPrefDateYear()) || key.matches(getPrefDateMonth())
                        || key.matches(getPrefDateDay())) {
                    updateDateSummary();
                } else if (key.matches(getPrefTimeHour()) || key.matches(getPrefTimeMinute())) {
                    updateTimeSummary();
                } else if (key.matches(getPrefAction())) {
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
        title.setSummary(sharedPreferences.getString(getPrefTitle(), ""));
    }

    private void updateMessageSummary() {
        message.setSummary(sharedPreferences.getString(getPrefMessage(), getString(R.string.pref_message_default)));
    }

    private void updateIconSummary() {
        int i = sharedPreferences.getInt(getPrefIcon(), -1);
        if (i != -1)
            icon.setIcon(i); // i is the resource id for the selected icon
    }

    private void updateDateSummary() {
        int year = sharedPreferences.getInt(getPrefDateYear(), 0);
        int month = sharedPreferences.getInt(getPrefDateMonth(), -1);
        int day = sharedPreferences.getInt(getPrefDateDay(), 0);


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
        int hour = sharedPreferences.getInt(getPrefTimeHour(), -1);
        int minute = sharedPreferences.getInt(getPrefTimeMinute(), -1);

        if (hour > -1 && minute > -1) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            ;
            time.setSummary(simpleDateFormat.format(c.getTime()));
        }
    }

    private void updateActionSummary() {
        String selection = sharedPreferences.getString(getPrefAction(), null);

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
        String pastPref = sharedPreferences.getString(getPrefMessage(), getString(R.string.pref_message_default));
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
                editor.putString(getPrefMessage(), editText.getText().toString());
                editor.commit();
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void showIconPickerDialog() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_icon_picker, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.pref_icon)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(view)
                .create();

        final GridView gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new IconPickerAdapter(context));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
                editor.putInt(getPrefIcon(), IconPickerAdapter.thumbs[position]);
                editor.commit();
                Log.i("CFDC", "Icon id: " + IconPickerAdapter.thumbs[position]);
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        BaseExtensionSettings baseExtensionSettings;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());


            int hour, minute;
            final Calendar c = Calendar.getInstance();
            // If no date has been chosen, use the current date
            if (prefs.getInt(baseExtensionSettings.getPrefTimeHour(), -5) == -5) {
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            } else {
                hour = prefs.getInt(baseExtensionSettings.getPrefTimeHour(), c.get(Calendar.HOUR_OF_DAY));
                minute = prefs.getInt(baseExtensionSettings.getPrefTimeMinute(), c.get(Calendar.MINUTE));
            }

            // Create a new instance of TimePickerDialog
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            editor = prefs.edit();

            Log.i("CfDC", "Hour: " + hourOfDay + " Minute: " + minute);

            editor.putInt(baseExtensionSettings.getPrefTimeHour(), hourOfDay);
            editor.putInt(baseExtensionSettings.getPrefTimeMinute(), minute);
            editor.commit();
        }

        public void setBaseExtension(BaseExtensionSettings baseExtensionSettings) {
            this.baseExtensionSettings = baseExtensionSettings;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        BaseExtensionSettings baseExtensionSettings;

        public DatePickerFragment() {
        }

        public void setBaseExtension(BaseExtensionSettings baseExtensionSettings) {
            this.baseExtensionSettings = baseExtensionSettings;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int year, month, day;
            Calendar c = Calendar.getInstance();
            // If no time has been chosen, use the current time
            if (prefs.getInt(baseExtensionSettings.getPrefDateYear(), -5) == -5) {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                year = prefs.getInt(baseExtensionSettings.getPrefDateYear(), c.get(Calendar.YEAR));
                month = prefs.getInt(baseExtensionSettings.getPrefDateMonth(), c.get(Calendar.MONTH));
                day = prefs.getInt(baseExtensionSettings.getPrefDateDay(), c.get(Calendar.DAY_OF_MONTH));
            }

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            editor = prefs.edit();

            Log.i("CfDC", "Year: " + year + " Month: " + monthOfYear + " Day: " + dayOfMonth);

            editor.putInt(baseExtensionSettings.getPrefDateYear(), year);
            editor.putInt(baseExtensionSettings.getPrefDateMonth(), monthOfYear);
            editor.putInt(baseExtensionSettings.getPrefDateDay(), dayOfMonth);
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

package com.davan.alarmcontroller.settings;
/**
 * Created by davandev on 2016-04-12.
 **/

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.WakeUpService;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setupActionBar();
        Context context = getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        final Preference pref = findPreference("checkbox");

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();

        // Add toolbar
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_main, root, false);
        root.addView(bar, 0); // insert at top
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setSubtitle("mytest");
            actionBar.setTitle("AlarmController Settings");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_help:
                // User chose the "help" item, show the app settings UI...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName)
    {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || ApplicationPreferenceFragment.class.getName().equals(fragmentName)
                || FibaroGeneralPreferenceFragment.class.getName().equals(fragmentName)
                || FibaroServerPreferenceFragment.class.getName().equals(fragmentName)
                || ExternalServerPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.compareTo("wake_up_service_enabled") == 0)
        {
            if (sharedPreferences.getBoolean(key, false))
            {
                startService(new Intent(SettingsActivity.this, WakeUpService.class));
            }
            else
            {
                stopService(new Intent(SettingsActivity.this, WakeUpService.class));
            }
        }

    }

    /**
     * This fragment shows fibaro server preferences only.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FibaroServerPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_fibaro_server);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("arm_alarm_scene"));
            bindPreferenceSummaryToValue(findPreference("disarm_alarm_scene"));
            bindPreferenceSummaryToValue(findPreference("arm_shell_scene"));
            bindPreferenceSummaryToValue(findPreference("disarm_shell_scene"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
    /**
     * This fragment shows external server preferences only.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ExternalServerPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_ext_server);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("ext_auth_server_address"));
            bindPreferenceSummaryToValue(findPreference("ext_disarm_alarm_url"));
            bindPreferenceSummaryToValue(findPreference("ext_arm_alarm_url"));
            bindPreferenceSummaryToValue(findPreference("ext_disarm_shell_url"));
            bindPreferenceSummaryToValue(findPreference("ext_arm_shell_url"));        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows application specific preferences only.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ApplicationPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general_settings);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("escaping_time"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
    /**
     * This fragment shows external server preferences only.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FibaroGeneralPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.fibaro_general_settings);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("fibaro_auth_server_address"));
            bindPreferenceSummaryToValue(findPreference("fibaro_variable_alarmtype"));
            bindPreferenceSummaryToValue(findPreference("fibaro_variable_alarmtype_fullhouse"));
            bindPreferenceSummaryToValue(findPreference("fibaro_variable_alarmtype_perimeter"));
            bindPreferenceSummaryToValue(findPreference("fibaro_variable_alarmstate"));
            bindPreferenceSummaryToValue(findPreference("fibaro_variable_alarmstate_armed"));
            bindPreferenceSummaryToValue(findPreference("fibaro_variable_alarmstate_disarmed"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

}

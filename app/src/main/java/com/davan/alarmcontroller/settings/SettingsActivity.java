package com.davan.alarmcontroller.settings;
/**
 * Created by davandev on 2016-04-12.
 **/

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.KeypadHttpService;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
        setupActionBar();
        Context context = getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "OnPostCreate");
        super.onPostCreate(savedInstanceState);

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
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "OnResume");
        Context context = getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        Context context = getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.unregisterOnSharedPreferenceChangeListener(this);

    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        Context context = getApplicationContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.unregisterOnSharedPreferenceChangeListener(this);
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
            actionBar.setTitle("ZenitGatekeeper Settings");
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
        Log.d(TAG,"onSharedPreferenceChanged:" + key);
        if (key.compareTo("http_service_enabled") == 0)
        {
            Log.d(TAG,"http_service_enabled");

            if (sharedPreferences.getBoolean(key, false))
            {
                startService(new Intent(SettingsActivity.this, KeypadHttpService.class));
            }
            else
            {
                stopService(new Intent(SettingsActivity.this, KeypadHttpService.class));

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

            bindPreferenceSummaryToValue(findPreference("virtual_device_id"));
            bindPreferenceSummaryToValue(findPreference("arm_alarm_button_id"));
            bindPreferenceSummaryToValue(findPreference("disarm_alarm_button_id"));
            bindPreferenceSummaryToValue(findPreference("arm_shell_button_id"));
            bindPreferenceSummaryToValue(findPreference("disarm_shell_button_id"));
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
            bindPreferenceSummaryToValue(findPreference("tts_callback_url"));
            bindPreferenceSummaryToValue(findPreference("escaping_time"));
            bindPreferenceSummaryToValue(findPreference("keypad_id"));
            bindPreferenceSummaryToValue(findPreference("battery_turn_off_charging"));
            bindPreferenceSummaryToValue(findPreference("battery_turn_on_charging"));
            bindPreferenceSummaryToValue(findPreference("tts_speech_speed"));
            bindPreferenceSummaryToValue(findPreference("announcement_file"));

            Preference sdPrefs = findPreference("select_audio_file");
            sdPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    int PICK_ANNOUNCEMENT = 1;
                    startActivityForResult(Intent.createChooser(intent, "Select Announcement..."), PICK_ANNOUNCEMENT);
                    return true;
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent audioReturnedIntent) {
            super.onActivityResult(requestCode, resultCode, audioReturnedIntent);

            if (resultCode == RESULT_OK) {
                Uri selectedAudio = audioReturnedIntent.getData();
                Log.d(TAG, "Announcement selected: " + selectedAudio.toString());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getActivity().grantUriPermission(
                            getActivity().getPackageName(),
                            selectedAudio,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    int takeFlags = audioReturnedIntent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getActivity().getContentResolver().takePersistableUriPermission(selectedAudio, takeFlags);
                }

                EditTextPreference announcementPrefs = (EditTextPreference)findPreference("announcement_file");
                announcementPrefs.setText(selectedAudio.toString());
            }
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
            bindPreferenceSummaryToValue(findPreference("fibaro_variable_alarmstate_breached"));

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

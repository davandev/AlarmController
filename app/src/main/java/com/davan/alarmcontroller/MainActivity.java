package com.davan.alarmcontroller;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.davan.alarmcontroller.camera.CameraActivity;
import com.davan.alarmcontroller.http.TelegramActivity;
import com.davan.alarmcontroller.http.WakeUpScreen;
import com.davan.alarmcontroller.http.WakeUpService;
import com.davan.alarmcontroller.http.WifiConnectionChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateListener;
import com.davan.alarmcontroller.power.PowerConnectionReceiver;
import com.davan.alarmcontroller.settings.AboutDialog;
import com.davan.alarmcontroller.settings.AlarmControllerResources;
import com.davan.alarmcontroller.settings.SettingsLauncher;

public class MainActivity extends AppCompatActivity implements AlarmStateListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private PowerManager.WakeLock mWakeLock = null;
    private WifiConnectionChecker wifiChecker;
    private AlarmControllerResources resources;
    private WakeUpScreen wakeUpScreen;
    private PowerConnectionReceiver powerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        wifiChecker = new WifiConnectionChecker(connMgr);

        resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(this),
                getSharedPreferences("com.davan.alarmcontroller.users", 0),
                getResources());
        //powerListener = new PowerConnectionReceiver();
        startServices();
    }

    private void startServices()
    {
        if (resources.isWakeUpServiceEnabled())
        {
            Log.d(TAG,"Starting WakeUpService");
            startService(new Intent(getBaseContext(), WakeUpService.class));
            wakeUpScreen = new WakeUpScreen(this);
        }
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "OnDestroy");
        super.onDestroy();
        if (mWakeLock != null)
        {
            mWakeLock.release();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the settings menu.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                SettingsLauncher.verifySettingsPassword(this, resources);
                return true;

            case R.id.action_about:
                AboutDialog.showDialog(this);
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void takePicture(View view)
    {
        Log.d(TAG,"takePicture");
        TelegramActivity telegram = new TelegramActivity(resources);
       telegram.sendMessage("test sending message");
        //telegram.sendPhoto();
       // Intent intent = new Intent(this, CameraActivity.class);
       // startActivity(intent);

    }

    /**
     * Verify configuration and start keypad
     * @param view
     */
    public void startAlarmKeypad(View view)
    {
        Log.d(TAG, "Starting AlarmKeyPad");
        try
        {
            resources.verifyConfiguration();
            if (!new AlarmStateChecker(wifiChecker, resources, this).updateAlarmState())
            {
                Log.d(TAG, "No wifi connection available");
                Toast.makeText(getBaseContext(), R.string.pref_message_no_network_connection, Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Configuration not ok");
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Receive callback with current alarm status
     * @param alarmState the current alarm state
     * @param alarmType the current alarm type
     */
    @Override
    public void alarmStateUpdate(String alarmState, String alarmType)
    {
        if (alarmState.compareTo(resources.getFibaroAlarmStateValueDisarmed()) == 0)
        {
            Intent intent = new Intent(this, Disarmed.class);
            startActivity(intent);
        }

        if (alarmState.compareTo(resources.getFibaroAlarmStateValueArmed()) == 0)
        {
            Intent intent = new Intent(this, Armed.class);
            intent.putExtra(getResources().getString(R.string.alarm_type), alarmType);
            startActivity(intent);
        }
    }
}
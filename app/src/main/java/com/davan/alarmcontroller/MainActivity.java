package com.davan.alarmcontroller;
/**
 * Created by davandev on 2016-04-12.
 */
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

import com.davan.alarmcontroller.http.WakeUpService;
import com.davan.alarmcontroller.http.WifiConnectionChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;
import com.davan.alarmcontroller.settings.SettingsLauncher;

public class MainActivity extends AppCompatActivity implements AlarmStateListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private PowerManager.WakeLock mWakeLock = null;
    private WifiConnectionChecker wifiChecker;
    private AlarmControllerResources resources;

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

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("wakeup-event"));
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                SettingsLauncher.verifySettingsPassword(this, resources, item.getActionView());
                return true;

            case R.id.action_about:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    // Method to start the service
/*    public void startService(View view)
    {
        Log.d(TAG, "Start service");
        startService(new Intent(getBaseContext(), WakeUpService.class));
    }
*/
    // Method to stop the service
  /*  public void stopService(View view)
    {
        Log.d(TAG, "Stop service");
        stopService(new Intent(getBaseContext(), WakeUpService.class));
    }
*/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "Received WakeUp");
            wakeUpScreen();
        }
    };

    public void wakeUpScreen()
    {
        Log.d(TAG, "WakeUp Screen");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        mWakeLock.acquire();
        Toast.makeText(getBaseContext(), "Screen on", Toast.LENGTH_SHORT).show();
    }

    public void startAlarmKeypad(View view)
    {
        Log.d(TAG, "Starting AlarmKeyPad");
        if( verifyConfiguration())
        {
            // Check if AlarmState has changed.
            if (!new AlarmStateChecker(wifiChecker, resources, this).updateAlarmState())
            {
                Log.d(TAG, "No wifi connection available");
                Toast.makeText(getBaseContext(), "No wifi connection available.", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
                Log.d(TAG, "Configuration not ok");
                Toast.makeText(getBaseContext(), "Configuration is not ok, update configuration.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean verifyConfiguration()
    {
        return true;
    }

    @Override
    public void alarmStateUpdate(String alarmState, String alarmType)
    {
        if (alarmState.compareTo(getResources().getString(R.string.alarm_state_disarmed)) == 0)
        {
            Intent intent = new Intent(this, Disarmed.class);
            startActivity(intent);
        }

        if (alarmState.compareTo(getResources().getString(R.string.alarm_state_armed)) == 0)
        {
            Intent intent = new Intent(this, Armed.class);
            intent.putExtra(getResources().getString(R.string.alarm_type), alarmType);
            startActivity(intent);
        }
    }
}
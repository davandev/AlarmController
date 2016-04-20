package com.davan.alarmcontroller;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.davan.alarmcontroller.http.WakeUpScreen;
import com.davan.alarmcontroller.http.WifiConnectionChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;
import com.davan.alarmcontroller.settings.SettingsLauncher;

public class Disarmed extends AppCompatActivity implements AlarmStateListener
{
    private static final String TAG = Disarmed.class.getSimpleName();

    private WifiConnectionChecker wifiChecker;
    private AlarmControllerResources resources;
    private WakeUpScreen wakeUpScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiChecker = new WifiConnectionChecker(connMgr);

        resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(this),
                getSharedPreferences("com.davan.alarmcontroller.users", 0),
                getResources());
        wakeUpScreen = new WakeUpScreen(this);

        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags ^= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        setContentView(R.layout.activity_disarmed);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(getLocalClassName(), "onResume");

        // Check if AlarmState has changed.
        if (!new AlarmStateChecker(wifiChecker, resources, this).updateAlarmState())
        {
            Log.d(TAG,"No wifi connection available");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // show menu when settings button is pressed
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_settings)
        {
            SettingsLauncher.verifySettingsPassword(this, resources);
        }
        else if (item.getItemId() == R.id.action_about)
        {
        }
        return true;
    }

    public void armSkalskydd(View view)
    {
        arm(getResources().getString(R.string.alarm_type_skalskydd));
    }

    public void armAlarm(View view)
    {
        arm(getResources().getString(R.string.alarm_type_alarm));
    }

    private void arm(String alarmType)
    {
        if (wifiChecker.isConnectionOk() )
        {
            Intent intent = new Intent(this, Arm.class);
            intent.putExtra(getResources().getString(R.string.alarm_type), alarmType);

            startActivity(intent);
        }
        else
        {
            Toast.makeText(getBaseContext(), R.string.pref_message_no_network_connection, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void alarmStateUpdate(String currentAlarmState, String currentAlarmType)
    {
        if (currentAlarmState.compareTo(resources.getFibaroAlarmStateValueArmed()) == 0)
        {
            Intent intent = new Intent(this, Armed.class);
            intent.putExtra(getResources().getString(R.string.alarm_type), currentAlarmType);
            startActivity(intent);
        }
    }
}
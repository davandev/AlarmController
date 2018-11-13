package com.davan.alarmcontroller;
/**
 * Created by davandev on 2016-04-12.
 * http://192.168.2.54/api/callAction?deviceID=69&name=pressButton&arg1=6
 **/
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.davan.alarmcontroller.http.WifiConnectionChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateListener;
import com.davan.alarmcontroller.settings.AboutDialog;
import com.davan.alarmcontroller.settings.AlarmControllerResources;
import com.davan.alarmcontroller.settings.SettingsLauncher;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;


public class Armed extends AppCompatActivity implements AlarmStateListener
{
    private static final String TAG = Armed.class.getSimpleName();

    private PowerManager.WakeLock mWakeLock = null;

    private String alarmType = "";
    private WifiConnectionChecker wifiChecker;
    private AlarmControllerResources resources;
    private float y1, y2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        alarmType = myIntent.getStringExtra(getResources().getString(R.string.alarm_type));
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags ^= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        setContentView(R.layout.activity_armed);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        wifiChecker = new WifiConnectionChecker(connMgr);
        resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(this),
                getSharedPreferences("com.davan.alarmcontroller.users", 0),
                getResources());
        setSettingsMenuListener();
        if (alarmType.compareTo(resources.getFibaroAlarmTypeValueFullHouseArmed()) == 0)
        {
            ((ImageButton)findViewById(R.id.imageButton)).setImageResource(R.drawable.locked_alarm);
        }
        else
        {
            ((ImageButton)findViewById(R.id.imageButton)).setImageResource(R.drawable.locked_shell);
        }

        Intent i = new Intent("sound-detection-event");
        i.putExtra("EventType", "start");
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

    }

    private void setSettingsMenuListener()
    {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().hide();
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        viewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case (MotionEvent.ACTION_DOWN) :
                        y1 = event.getY();

                        return true;
                    case (MotionEvent.ACTION_UP) :
                        y2 = event.getY();
                        float deltaX = y2 - y1;
                        if (deltaX < 0)
                        {
                            getSupportActionBar().hide();
                        }
                        else if(deltaX >0)
                        {
                            getSupportActionBar().show();
                        }
                        return true;
                    default :
                        return false;
                }
            }
        });
    }
    /**
     * OnResume, check the current alarmstate, it might have changed.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");

        if (!new AlarmStateChecker(wifiChecker, resources, this).updateAlarmState())
        {
            Log.d(TAG,"No wifi connection available");
            Toast.makeText(getBaseContext(), R.string.pref_message_no_network_connection, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // show menu when menu button is pressed
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
            AboutDialog.showDialog(this);
        }
        return true;
    }

    /**
     * Disarm the alarm.
     */
    public void disarmAlarm(View view)
    {
        Log.d(TAG, "Disarm alarm");
        Intent intent = new Intent(this, Disarm.class);
        intent.putExtra(getResources().getString(R.string.alarm_type), alarmType);

        startActivity(intent);
    }

    /**
     * Receive callback after fetching the current alarm state from fibaro system.
     * @param alarmState the current state of the alarm
     * @param alarmType the current alarm type
     */
    @Override
    public void alarmStateUpdate(String alarmState, String alarmType)
    {
        if (alarmState.compareTo(resources.getFibaroAlarmStateValueBreached() ) == 0)
        {
            ((ImageButton)findViewById(R.id.imageButton)).setImageResource(R.drawable.breached);
        }
        if (alarmState.compareTo(resources.getFibaroAlarmStateValueDisarmed()) == 0)
        {
            Log.d(TAG,"Alarm is already disarmed");
            Intent intent = new Intent(this, Disarmed.class);
            startActivity(intent);
        }
    }
}

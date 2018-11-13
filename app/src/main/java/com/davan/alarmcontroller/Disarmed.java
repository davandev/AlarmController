package com.davan.alarmcontroller;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import com.davan.alarmcontroller.http.services.WakeUpScreen;
import com.davan.alarmcontroller.http.WifiConnectionChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateListener;
import com.davan.alarmcontroller.settings.AboutDialog;
import com.davan.alarmcontroller.settings.AlarmControllerResources;
import com.davan.alarmcontroller.settings.SettingsLauncher;
import com.davan.alarmcontroller.http.services.TtsCreator;

public class Disarmed extends AppCompatActivity implements AlarmStateListener
{
    private static final String TAG = Disarmed.class.getSimpleName();

    private WifiConnectionChecker wifiChecker;
    private AlarmControllerResources resources;
    private float y1, y2;

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
        setContentView(R.layout.activity_disarmed);

        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags ^= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        setSettingsMenuListener();
        Intent i = new Intent("sound-detection-event");
        i.putExtra("EventType", "stop");
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
    public void armSkalskydd(View view)
    {
        arm(resources.getFibaroAlarmTypeValuePerimeterArmed());
    }

    public void armAlarm(View view)
    {
        arm(resources.getFibaroAlarmTypeValueFullHouseArmed());
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
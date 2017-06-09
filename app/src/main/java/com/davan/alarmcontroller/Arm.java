package com.davan.alarmcontroller;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.davan.alarmcontroller.procedures.AlarmProcedureIf;
import com.davan.alarmcontroller.procedures.AlarmProcedureFactory;
import com.davan.alarmcontroller.procedures.AlarmProcedureResultListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

import java.util.Random;

public class Arm extends AppCompatActivity implements AlarmProcedureResultListener
{
    private static final String TAG = Arm.class.getSimpleName();

    private String alarmType = "0";
    private AlarmControllerResources resources;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags ^= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        Intent myIntent = getIntent();
        // Alarmtype to arm
        alarmType = myIntent.getStringExtra(getResources().getString(R.string.alarm_type));
        resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(this),
                getSharedPreferences("com.davan.alarmcontroller.users", 0),
                getResources());
        try
        {
            //Create alarm procedure, depending on configuration
            AlarmProcedureIf handler = AlarmProcedureFactory.createProcedure(resources, this);
            // Arm alarm
            handler.arm(alarmType);

            if(alarmType.compareTo(resources.getFibaroAlarmTypeValueFullHouseArmed()) ==0 )
            {
                setContentView(R.layout.activity_arming);
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getBaseContext(), R.string.pref_message_no_server_enabled, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, Disarmed.class);
            startActivity(intent);
        }
    }

    /**
     * Change background color of screen to a random color.
     */
    private void changeBackGroundColor()
    {
        View view = this.getWindow().getDecorView();
        Random rand = new Random();
        view.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
    }

    /**
     * Alarm is armed, change to armed state activity
     */
    private void armed()
    {
        Intent intent = new Intent(this, Armed.class);
        intent.putExtra(getResources().getString(R.string.alarm_type), alarmType);
        startActivity(intent);
    }

    /**
     * Start countdown, change background color once every second until escaping time
     * has passed, then change to armed state activity
     */
    private void startArmingCountDown()
    {
        int escapeTime = Integer.parseInt(resources.getEscapingTime());
        new CountDownTimer(escapeTime*1000, 100)
        {
            public void onTick(long millisUntilFinished)
            {
                ((TextView) findViewById(R.id.countDownField)).setText("" + millisUntilFinished / 1000);
                changeBackGroundColor();
            }

            public void onFinish()
            {
                Log.d(TAG,"Arming finished,");
                armed();
            }
        }.start();
    }

    /**
     * Arming callback result received,
     * @param success true if arming was successful
     * @param result
     */
    @Override
    public void resultReceived(boolean success, String result)
    {
        if (alarmType.compareTo(resources.getFibaroAlarmTypeValuePerimeterArmed()) == 0)
        {
            armed();
        }
        else
        {
            startArmingCountDown();
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.RED);
        }
    }
}

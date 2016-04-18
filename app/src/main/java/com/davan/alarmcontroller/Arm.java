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

import com.davan.alarmcontroller.authentication.AlarmProcedureIf;
import com.davan.alarmcontroller.authentication.AlarmProcedureFactory;
import com.davan.alarmcontroller.authentication.AlarmProcedureResultListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

import java.util.Random;

public class Arm extends AppCompatActivity implements AlarmProcedureResultListener
{
    private static final String TAG = Arm.class.getSimpleName();
    private AlarmProcedureIf handler;
    private String alarmType = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags ^= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        Intent myIntent = getIntent();
        alarmType = myIntent.getStringExtra(getResources().getString(R.string.alarm_type));
        AlarmControllerResources resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(this),
                getSharedPreferences("com.davan.alarmcontroller.users", 0),
                getResources());
        try
        {
            handler = AlarmProcedureFactory.createProcedure(resources, this);
            handler.arm(alarmType);

            if(alarmType.compareTo(getResources().getString(R.string.alarm_type_alarm)) ==0 )
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

/*        if (alarmType.compareTo(getResources().getString(R.string.alarm_type_skalskydd)) == 0)
        {
            armed();
        }
        else
        {
            startArmingCountDown();
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.RED);
        }
*/
    }
    public void changeBackGroundColor()
    {
        View view = this.getWindow().getDecorView();
        Random rand = new Random();
        view.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
    }

    public void armed()
    {
        Intent intent = new Intent(this, Armed.class);
        intent.putExtra(getResources().getString(R.string.alarm_type), alarmType);
        startActivity(intent);
    }

    public void startArmingCountDown()
    {
        new CountDownTimer(30000, 100)
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

    @Override
    public void resultReceived(boolean success, String result)
    {
        if (alarmType.compareTo(getResources().getString(R.string.alarm_type_skalskydd)) == 0)
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

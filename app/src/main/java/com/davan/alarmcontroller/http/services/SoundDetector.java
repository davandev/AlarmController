package com.davan.alarmcontroller.http.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import com.davan.alarmcontroller.http.TelegramActivity;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by davandev on 2017-10-19.
 */

public class SoundDetector extends TimerTask {
    /*
     * Register to received tts requests
     */
    private static final String TAG = SoundDetector.class.getSimpleName();

    private final AlarmControllerResources resources;
    private int pollIntervall;
    private double thresholdLevel;
    private Timer timer;
    private MediaRecorder mRecorder = null;

    public SoundDetector(AlarmControllerResources res)
    {
        Log.d(TAG, "Create SoundDetector");
        resources = res;
        pollIntervall = Integer.parseInt(resources.getSoundDetectionPollIntervall())*1000;
        thresholdLevel = Double.parseDouble(resources.getSoundDetectionThresholdLevel());
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "Received sound-detection-event");
            String type = intent.getStringExtra("EventType");
            if (type.compareTo("start") ==0)
            {
                startDetection();
            }
            else
            {
                stopDetection();
            }
        }
    };
    @Override
    public void run() {
        double currentValue = getAmplitude();
        if (currentValue >= thresholdLevel)
        {
            Log.d(TAG, "Currentvalue exceeds threshold value :" + Double.toString(currentValue) );
            TelegramActivity activity = new TelegramActivity(resources);
            activity.sendMessage("Keypad[" + resources.getKeypadId() + "] har upptäckt ljud");
        }
    }

    public void stopDetection() {
        Log.d(TAG, "Stop sound detection");
        TelegramActivity activity = new TelegramActivity(resources);
        activity.sendMessage("Stopping sound detection");

        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    public void startDetection() {
        Log.d(TAG, "Start sound detection");
        TelegramActivity activity = new TelegramActivity(resources);
        activity.sendMessage("Starting sound detection");

        if (mRecorder == null) {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");

            try {
                mRecorder.prepare();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mRecorder.start();
            timer = new Timer();
            timer.scheduleAtFixedRate(this, pollIntervall, pollIntervall);
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return   20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;
    }
    /*
     * Register to received wakeup events
     */
    public void registerForEvents(Context context)
    {
        Log.d(TAG, "Register for sound-detection-event");

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("sound-detection-event"));
    }
    public void unregisterForEvents(Context context)
    {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(
                mMessageReceiver);
    }
}

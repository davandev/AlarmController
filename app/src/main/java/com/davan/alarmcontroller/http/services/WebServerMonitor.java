package com.davan.alarmcontroller.http.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.davan.alarmcontroller.http.util.WebServerControlListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by davandev on 2017-10-19.
 */

public class WebServerMonitor extends TimerTask {
    /*
     * Register to received tts requests
     */
    private static final String TAG = WebServerMonitor.class.getSimpleName();

    private final AlarmControllerResources resources;
    private Context context;
    private boolean pingReceived = false;
    private WebServerControlListener listener;
    private static Timer timer = new Timer();

    public WebServerMonitor(WebServerControlListener receiver, AlarmControllerResources res)
    {
        Log.d(TAG, "Create WebServerMonitor");
        resources = res;
        listener = receiver;
        int THIRTY_MINUTES = 1800000;
        timer.scheduleAtFixedRate(this, THIRTY_MINUTES, THIRTY_MINUTES);

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "Received ping request");
            if (resources.isRestartWebserverServiceEnabled()) {
                pingReceived = true;
            }

        }
    };

    public void registerForEvents(Context context)
    {
            LocalBroadcastManager.getInstance(context).registerReceiver(
                    mMessageReceiver, new IntentFilter("ping-event"));
    }
    /*
     * Unregister to received tts requests
     */
    public void unregisterForEvents(Context context)
    {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(
                mMessageReceiver);
    }

    @Override
    public void run() {
        if (resources.isRestartWebserverServiceEnabled()) {
            Log.d(TAG, "Timeout received, check ping");
            if (!pingReceived) {
                Log.i(TAG, "No ping received, restart webserver");
                listener.restartWebServer();
            }
            pingReceived = false;
        }
    }
}

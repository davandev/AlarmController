package com.davan.alarmcontroller.http.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by davandev on 2016-04-18.
 *
 * Responsible to turn on screen when a http request wakeup request has
 * been received.
 * Registers as receiver for wakeup-event's
 **/
public class WakeUpScreen
{
    private static final String TAG = WakeUpScreen.class.getSimpleName();

    public WakeUpScreen()
    {
        Log.i(TAG,"Register for wakeup callbacks");
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "Received WakeUp event");
            wakeUpScreen(context);
        }
    };

    /**
     * Received request to wakeup screen.
     * @param context
     */
    public void wakeUpScreen(Context context)
    {
        Log.i(TAG, "WakeUp Screen");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock mWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP, "Wakeup");

        mWakeLock.acquire();
        mWakeLock.release();
    }

    /*
     * Register to received wakeup events
     */
    public void registerForEvents(Context context)
    {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("wakeup-event"));
    }
    public void unregisterForEvents(Context context)
    {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(
                mMessageReceiver);
    }
}

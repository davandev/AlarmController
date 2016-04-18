package com.davan.alarmcontroller.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by davandev on 2016-04-18.
 **/
public class WakeUpScreen
{
    private static final String TAG = WakeUpScreen.class.getSimpleName();

    public WakeUpScreen(Context context)
    {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("wakeup-event"));

    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "Received WakeUp");
            wakeUpScreen(context);
        }
    };
    public void wakeUpScreen(Context context)
    {
        Log.d(TAG, "WakeUp Screen");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Wakeup");
        mWakeLock.acquire();
        mWakeLock.release();
    }
}

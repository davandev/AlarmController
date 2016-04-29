package com.davan.alarmcontroller.power;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.TelegramActivity;
import com.davan.alarmcontroller.http.WakeUpService;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

/**
 * Created by davandev on 2016-04-23.
 **/
public class PowerConnectionReceiver
{
    private static final String TAG = PowerConnectionReceiver.class.getName();

    private int mBatteryLevel;
    private IntentFilter mBatteryLevelFilter;
    private AlarmControllerResources resources;
    private boolean shouldSendTelegram = true;

    public PowerConnectionReceiver(AlarmControllerResources res)
    {
        resources = res;
    }

    BroadcastReceiver mBatteryReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            if (mBatteryLevel < 10)
            {
                if (resources.isTelegramEnabled())
                {
                    if (shouldSendTelegram)
                    {
                        TelegramActivity activity = new TelegramActivity(resources);
                        activity.sendMessage(context.getString(R.string.pref_message_low_battery_level));
                        shouldSendTelegram = false;
                    }
                }
            }
            else
            {
                shouldSendTelegram = true;
            }
        }
    };


    public void registerMyReceiver(Context context)
    {
        mBatteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(mBatteryReceiver, mBatteryLevelFilter);
    }
}
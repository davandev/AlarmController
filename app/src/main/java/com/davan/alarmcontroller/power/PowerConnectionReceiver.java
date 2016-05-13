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
import com.davan.alarmcontroller.procedures.CustomSceneProcedure;
import com.davan.alarmcontroller.procedures.CustomSceneProcedureResultListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

/**
 * Created by davandev on 2016-04-23.
 **/
public class PowerConnectionReceiver implements CustomSceneProcedureResultListener {
    private static final String TAG = PowerConnectionReceiver.class.getName();

    private int mBatteryLevel;
    private IntentFilter mBatteryLevelFilter;
    private AlarmControllerResources resources;
    private boolean shouldInvokeAction = true;
    private CustomSceneProcedure sceneProcedure;

    public PowerConnectionReceiver(AlarmControllerResources res)
    {
        resources = res;
    }

    BroadcastReceiver mBatteryReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            if (mBatteryLevel < 15)
            {
                handleLowBatteryLevel(context);
            }
            else if (mBatteryLevel > 95)
            {
                handleHighBatteryLevel(context);
            }
            else
            {
                shouldInvokeAction = true;
            }
        }
    };

    /**
     * Low battery level, invoke scene on fibaro system
     * if enabled. The scene could do anything, but the
     * intention is to turn on a wallsocket and start charging
     * the device.
     * @param context
     */
    public void handleLowBatteryLevel(Context context)
    {
        if (shouldInvokeAction)
        {
            if (resources.isTelegramEnabled())
            {
                TelegramActivity activity = new TelegramActivity(resources);
                activity.sendMessage("Keypad["+resources.getKeypadId()+"]: " +context.getString(R.string.pref_message_low_battery_level));
            }
            if (resources.isChargingControlEnabled())
            {
                sceneProcedure = new CustomSceneProcedure(resources, this);
                sceneProcedure.runScene(resources.getTurnOnChargingSceneId());
            }
            shouldInvokeAction = false;
        }
    }

    /**
     * Battery level ok, invoke scene on fibaro system
     * if enabled. The scene could do anything, but the
     * intention is to turn off a wallsocket and stop charging
     * the device.
     * @param context
     */
    public void handleHighBatteryLevel(Context context)
    {
        if (shouldInvokeAction)
        {
            if (resources.isTelegramEnabled())
            {
                TelegramActivity activity = new TelegramActivity(resources);
                activity.sendMessage("Keypad["+resources.getKeypadId()+"]: "+context.getString(R.string.pref_message_battery_level_ok));
            }
            if (resources.isChargingControlEnabled())
            {
                sceneProcedure = new CustomSceneProcedure(resources, this);
                sceneProcedure.runScene(resources.getTurnOffChargingSceneId());
            }
            shouldInvokeAction = false;
        }
    }

    /**
     * Register to receive battery level changes.
     * @param context
     */
    public void registerMyReceiver(Context context)
    {
        mBatteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(mBatteryReceiver, mBatteryLevelFilter);
    }
}
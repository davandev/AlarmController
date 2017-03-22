package com.davan.alarmcontroller.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.TelegramActivity;
import com.davan.alarmcontroller.procedures.CustomSceneProcedure;
import com.davan.alarmcontroller.procedures.CustomSceneProcedureResultListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

/**
 * Created by davandev on 2016-04-23.
 **/
public class PowerConnectionReceiver implements CustomSceneProcedureResultListener {
    private static final String TAG = PowerConnectionReceiver.class.getName();

    private IntentFilter mBatteryLevelFilter;
    private final AlarmControllerResources resources;
    private boolean shouldInvokeAction = true;
    private CustomSceneProcedure sceneProcedure;

    public PowerConnectionReceiver(AlarmControllerResources res)
    {
        resources = res;
    }

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            int mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            if (mBatteryLevel < 20)
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
    private void handleLowBatteryLevel(Context context)
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
    private void handleHighBatteryLevel(Context context)
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
    public void registerForEvents(Context context)
    {
        mBatteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(mBatteryReceiver, mBatteryLevelFilter);
    }

    /**
     * Unregister for battery level changes
     * @param context
     */
    public void unregisterForEvents(Context context)
    {
        mBatteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.unregisterReceiver(mBatteryReceiver);
    }

}
package com.davan.alarmcontroller.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import com.davan.alarmcontroller.R;

/**
 * Created by davandev on 2016-04-23.
 **/
public class PowerConnectionReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("PowerCOnnectionReciever","onrecive");
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging) {
            Toast.makeText(context, "Power is connected", Toast.LENGTH_LONG).show();
        } else
        {
            Toast.makeText(context, "Power is disconnected", Toast.LENGTH_LONG).show();
        }
    }
}
package com.davan.alarmcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.davan.alarmcontroller.settings.AlarmControllerResources;

/**
 * Created by davandev on 2017-06-05.
 */

public class BootupReceiver extends BroadcastReceiver {
    private static final String TAG = BootupReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmControllerResources resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(context),
                context.getSharedPreferences("com.davan.alarmcontroller.users", 0),
                context.getResources());

        if(resources.getAutoStartAtReboot())
        {
            Log.d(TAG, "Autostart enabled");
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.app_cache_file), Context.MODE_PRIVATE);
            String restartAppKey = context.getResources().getString(R.string.restart_app);
            String restartApp = sharedPref.getString(restartAppKey,"ttsmode");
            Log.d(TAG,"RestartApp:" + restartApp);
            if (restartApp.compareTo("alarmcontroller") == 0)
            {
                Log.d(TAG, "Starting alarmcontroller");
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
            else
            {
                Log.d(TAG, "Starting tts mode");
                Intent i = new Intent(context, TtsMode.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
        else
        {
            Log.d(TAG, "Autostart disabled");
        }
    }
}
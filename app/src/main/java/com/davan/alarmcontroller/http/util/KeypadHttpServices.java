package com.davan.alarmcontroller.http.util;

import android.content.Context;
import android.util.Log;

import com.davan.alarmcontroller.http.services.LocalMediaFilePlayer;
import com.davan.alarmcontroller.http.services.TtsCreator;
import com.davan.alarmcontroller.http.services.TtsReader;
import com.davan.alarmcontroller.http.services.WakeUpScreen;
import com.davan.alarmcontroller.power.PowerConnectionReceiver;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

/**
 * Created by davandev on 2017-04-24.
 */

public class KeypadHttpServices {
    private static final String TAG = KeypadHttpServices.class.getSimpleName();

    private TtsCreator ttsCreator = null;
    private TtsReader ttsReader = null;
    private LocalMediaFilePlayer mediaPlayer = null;
    private PowerConnectionReceiver powerListener = null;
    private WakeUpScreen wakeUpScreen = null;

    private final AlarmControllerResources resources;

    public KeypadHttpServices(AlarmControllerResources res)
    {
        resources = res;
    }

    public void createServices(Context context) {
        Log.d(TAG, "Create and register services");

        mediaPlayer = new LocalMediaFilePlayer(context);
        mediaPlayer.registerForEvents(context);

        wakeUpScreen = new WakeUpScreen(resources);
        wakeUpScreen.registerForEvents(context);

        ttsCreator = new TtsCreator(resources);
        ttsCreator.registerForEvents(context);

        ttsReader = new TtsReader(context, resources);
        ttsReader.registerForEvents(context);

        powerListener = new PowerConnectionReceiver(resources);
        powerListener.registerForEvents(context);

    }

    public void destroyServices(Context context)
    {
        Log.d(TAG, "Deregister services");

        if (mediaPlayer != null) {
            mediaPlayer.unregisterForEvents(context);
        }
        if (powerListener != null) {
            powerListener.unregisterForEvents(context);
        }
        if (wakeUpScreen != null) {
            wakeUpScreen.unregisterForEvents(context);
        }
        if (ttsCreator != null) {
            ttsCreator.unregisterForEvents(context);
        }
        if (ttsReader != null) {
            ttsReader.unregisterForEvents(context);
        }
    }
}

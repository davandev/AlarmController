package com.davan.alarmcontroller.http.util;

import android.content.Context;
import android.util.Log;

import com.davan.alarmcontroller.http.services.LocalMediaFilePlayer;
import com.davan.alarmcontroller.http.services.TtsCreator;
import com.davan.alarmcontroller.http.services.TtsReader;
import com.davan.alarmcontroller.http.services.WakeUpScreen;
import com.davan.alarmcontroller.http.services.WebServerMonitor;
import com.davan.alarmcontroller.power.PowerConnectionReceiver;
import com.davan.alarmcontroller.settings.AlarmControllerResources;
import com.davan.alarmcontroller.http.services.SoundDetector;
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
    private WebServerMonitor serverMonitor = null;
    private SoundDetector soundDetector = null;

    private final AlarmControllerResources resources;

    public KeypadHttpServices(AlarmControllerResources res)
    {
        resources = res;
    }

    public void createServices(Context context, WebServerControlListener listener) {
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

        serverMonitor = new WebServerMonitor(listener, resources);
        serverMonitor.registerForEvents(context);

        soundDetector = new SoundDetector(resources);
        soundDetector.registerForEvents(context);
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
        if (serverMonitor!= null) {
            serverMonitor.unregisterForEvents(context);
        }
        if (soundDetector!= null) {
            soundDetector.unregisterForEvents(context);
        }

    }
}

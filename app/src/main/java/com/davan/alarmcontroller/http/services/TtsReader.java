package com.davan.alarmcontroller.http.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.davan.alarmcontroller.settings.AlarmControllerResources;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by davandev on 2017-03-09.
 *
 * TTS reader will generate speech from a text and play the speech
 * in the internal device speaker.
 **/
public class TtsReader implements TextToSpeech.OnInitListener
{
    private static final String TAG = TtsReader.class.getSimpleName();

    private TextToSpeech t1;
    // Configuration
    private final AlarmControllerResources resources;
    private String message;

    public TtsReader(Context context, AlarmControllerResources res)
    {
        Log.d(TAG, "Create TtsReader");
        resources = res;
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int configuredVolumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG,"MaxVolumeLevel["+maxVolume+"] CurrentVolumeLevel["+configuredVolumeLevel+"]");
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "Received tts request");
            initTts(context, intent);
        }
    };

    /**
     * Set volume level of the device
     * @param context
     * @param level new volume level
     */
    private void setVolumeLevel(Context context, int level)
    {
        Log.d(TAG,"Set new volume level[" + level +"]");

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0);
    }

    private void initTts(Context context, Intent intent)
    {
        if (resources.isTtsPlayOnDeviceEnabled())
        {
            message = intent.getStringExtra("message");
            if (message.contains("&vol="))
            {
                String[] res = message.split("&vol=");
                message = res[0];
                setVolumeLevel(context, Integer.parseInt(res[1]));
            }
            Log.i(TAG, "Generate tts: " + message);
            t1 = new TextToSpeech(context, this);
        } else
        {
            Log.d(TAG," Play on device is disabled");
        }
    }

    /**
     * Received request to generate a TTS wav file.
     */
    private void generateTts() {
            Log.i(TAG, "Generate tts: " + message);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Bundle params = new Bundle();
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ttsToSpeak");
                t1.speak(message, TextToSpeech.QUEUE_ADD, params, "ttsToSpeak");
            } else {
                HashMap<String, String> hashTts = new HashMap<>();
                hashTts.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ttsToSpeak");
                t1.speak(message, TextToSpeech.QUEUE_ADD, hashTts);
            }
        }
    @Override
    public void onInit(int status)
    {
        if(status != TextToSpeech.ERROR)
        {
            generateTts();
        }
    }

    /*
     * Register to received tts requests
     */
    public void registerForEvents(Context context)
    {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("tts-event"));
    }
    /*
     * Unregister to received tts requests
     */
    public void unregisterForEvents(Context context)
    {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(
                mMessageReceiver);
    }
}

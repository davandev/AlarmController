package com.davan.alarmcontroller.http.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.davan.alarmcontroller.http.RequestDispatcher;
import com.davan.alarmcontroller.http.RequestDispatcherResultListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

import java.io.File;
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
    private AlarmControllerResources resources;

    public TtsReader(Context context, AlarmControllerResources res)
    {
        Log.d(TAG, "Register for tts requests");
        resources = res;

        t1 = new TextToSpeech(context.getApplicationContext(), this);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "Received tts request");
            generateTts(context, intent);
        }
    };

    /**
     * Received request to generate a TTS wav file.
     * @param context
     * @param intent
     */
    public void generateTts(Context context, Intent intent) {
        String toSpeak = intent.getStringExtra("message");
        Log.d(TAG, "Generate tts: " + toSpeak);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Bundle params = new Bundle();
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ttsToSpeak");
                t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, params, "ttsToSpeak");
            }
            else
            {
                HashMap<String, String> hashTts = new HashMap<String, String>();
                hashTts.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ttsToSpeak");
                t1.speak(toSpeak, TextToSpeech.QUEUE_ADD, hashTts);
            }
        }
    @Override
    public void onInit(int status)
    {
        if(status != TextToSpeech.ERROR)
        {
            // Language does not seem to matter when a custom TTS engine is selected
            t1.setLanguage(Locale.UK);
//            Bundle params = new Bundle();
//            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ttsToSpeak");
//            t1.speak("Text to speech is now initialized", TextToSpeech.QUEUE_FLUSH, params, "ttsToSpeak");

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

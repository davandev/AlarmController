package com.davan.alarmcontroller.http.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.davan.alarmcontroller.settings.AlarmControllerResources;

import java.io.File;
import java.io.IOException;
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
    private Context context;

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
            this.context = context;
            message = intent.getStringExtra("message");
            if (message.contains("&vol="))
            {
                String[] res = message.split("&vol=");
                message = res[0];
                setVolumeLevel(context, Integer.parseInt(res[1]));
            }

            if (resources.isPlayAnnouncementOnDeviceEnabled())
            {
                playAnnouncement();
            }
            else
            {
                playSpeech(message);
            }
        } else
        {
            Log.d(TAG," Play on device is disabled");
        }
    }

    /**
     * Start generating speech from message
     * @param message text message to generate speech
     */
    private void playSpeech(String message)
    {
        Log.i(TAG, "Generate tts: " + message);
        t1 = new TextToSpeech(context, this);
    }

    /**
     * Play announcement message before playing the speech
     */
    private void playAnnouncement()
    {
        Log.i(TAG, "Play announcement message");
        try {
            MediaPlayer mediaPlayer = new android.media.MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    playSpeech(message);
                }

            });
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(context, Uri.parse(resources.getAnnouncementFile()));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Received request to generate a TTS wav file.
     */
    private void generateTts() {
            Log.i(TAG, "Generate tts: " + message);
            t1.setSpeechRate(Float.parseFloat(resources.getTtsSpeechRate()));

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

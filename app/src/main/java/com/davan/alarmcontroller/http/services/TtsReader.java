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
import android.speech.tts.UtteranceProgressListener;
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
 * If configured it can play an announcement message before playing speech
 * If configured it can "play" silence for a configured time, to solve the
 * problem when part of the speech is cut due to connection to a bluetooth speaker
 * is not setup fast enough
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
        if (!resources.isTtsPlayOnDeviceEnabled())
        {
            Log.d(TAG," Play on device is disabled");
        }

        this.context = context;
        message = intent.getStringExtra("message");
        if (message.contains("&vol="))
        {
            String[] res = message.split("&vol=");
            message = res[0];
            setVolumeLevel(context, Integer.parseInt(res[1]));
        }

        t1 = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status)
    {
        if(status != TextToSpeech.ERROR)
        {
            t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId)
                {
                    if (utteranceId.equals("ttsSilence")) {
                        Log.d(TAG, "Silence played");
                        postSilence();
                    }
                    if (utteranceId.equals("ttsToSpeak")) {
                        Log.d(TAG, "Message played");
                        t1.stop();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.w(TAG, "TTS generation failed");
                }

                @Override
                public void onStart(String utteranceId) {
                    Log.i(TAG, "TTS generation started");
                }
            });

            if (resources.isPlaySilenceEnabled() &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                Log.d(TAG, "Play silence for " + Long.toString(resources.getSilenceTime()) + " milliseconds");
                t1.playSilentUtterance(resources.getSilenceTime(),TextToSpeech.QUEUE_ADD,"ttsSilence");
            }
            else
            {
                postSilence();
            }
    }
    }
    /**
    * Continue with playing announcement and message
    */
    public void postSilence()
    {
        if(resources.isPlayAnnouncementOnDeviceEnabled())
        {
            playAnnouncement();
        }
        else
        {
            playSpeech();
        }
    }
    /**
     * Start generating speech from message
     */
    private void playSpeech()
    {
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
                    playSpeech();
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

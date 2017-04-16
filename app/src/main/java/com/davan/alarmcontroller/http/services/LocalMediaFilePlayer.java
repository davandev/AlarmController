package com.davan.alarmcontroller.http.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
/**
 * Created by davandev on 2017-04-13.
 */

public class LocalMediaFilePlayer {

    private static final String TAG = LocalMediaFilePlayer.class.getSimpleName();
    private float maxVolumeLevel = 15;

    public LocalMediaFilePlayer(Context context)
    {
        Log.d(TAG, "Create MediaPlayer");
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG,"MaxVolumeLevel["+maxVolumeLevel+"]");
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String message = intent.getStringExtra("message");
            playMedia(context, message);
        }
    };

    private void playMedia(Context context, String message)
    {
        Log.i(TAG, "Received play request");
        try {
            float volume = -1;
            if (message.contains("&vol="))
            {
                String[] res = message.split("&vol=");
                message = res[0];
                volume = Integer.parseInt(res[1])/maxVolumeLevel;
                Log.d(TAG, "New volume:" + volume);
            }

            File mediaFile = new File(message);
            if (!mediaFile.exists())
            {
                Log.w(TAG,"File :" + mediaFile.getAbsolutePath() +" does not exist");
                return;
            }

            Uri myUri = Uri.fromFile(mediaFile);
            MediaPlayer mediaPlayer = new android.media.MediaPlayer();
            if (volume != -1)
            {
                mediaPlayer.setVolume(volume,volume);
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(context, myUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch(IOException e)
        {
            Log.w(TAG,"Failed to play media:" + e.getMessage());
        }
    }
    /*
    * Register to received tts requests
    */
    public void registerForEvents(Context context)
    {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("play-event"));
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

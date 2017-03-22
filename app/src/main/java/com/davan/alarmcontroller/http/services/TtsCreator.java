package com.davan.alarmcontroller.http.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
 * TtsCreator will generate speech from text, store the speech in a file and notify
 * a configure callback receiver.
 **/
public class TtsCreator implements TextToSpeech.OnInitListener, RequestDispatcherResultListener
{
    private static final String TAG = TtsCreator.class.getSimpleName();

    private TextToSpeech t1;
    // Configuration
    private final AlarmControllerResources resources;
    private Context myContext;
    private String message;

    public TtsCreator(AlarmControllerResources res)
    {
        Log.i(TAG, "Register for tts requests");
        resources = res;
        //myContext= context;
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "Received tts request");
            //generateTts(context, intent);
            initTts(context,intent);
        }
    };

    private void initTts(Context context, Intent intent)
    {
        message = intent.getStringExtra("message");
        Log.i(TAG, "Generate tts: " + message);
        myContext = context;
        t1 = new TextToSpeech(context, this);
    }
    /**
     * Received request to generate a TTS wav file.
     */
    //public void generateTts(Context context, Intent intent) {
    private void generateTts() {
        //String toSpeak = intent.getStringExtra("message");
        //Log.i(TAG, "Generate tts: " + toSpeak);
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                resources.getTtsStorageFolder());
        
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.w(TAG, "Failed to create mediadir");
            }
            else
            {
                Log.d(TAG, "Created mediadir" + mediaStorageDir.getAbsolutePath());
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + resources.getTtsFileName());
        Log.d(TAG, "TTS path : " + mediaFile.getAbsolutePath());

        HashMap<String, String> hashTts = new HashMap<>();
        hashTts.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ttsToFile");
        t1.synthesizeToFile(message, hashTts, mediaFile.getAbsolutePath());

    }
    @Override
    public void onInit(int status)
    {
        if(status != TextToSpeech.ERROR)
        {
            // Language does not seem to matter when a custom TTS engine is selected
            t1.setLanguage(Locale.UK);

            t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    Log.i(TAG, "TTS file created successfully");

                    if (utteranceId.equals("ttsToFile")) {
                        Log.d(TAG, "Notify listener about the finished tts file");
                        notifyTtsCompleted();
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
            generateTts();
        }
    }

    /**
     * Notify configured tts callback receiver that TTS generation is completed.
     */
    private void notifyTtsCompleted()
    {
        //if(resources.getTtsCallbackUrl().compareTo())
        RequestDispatcher dispatcher = new RequestDispatcher(this);
        dispatcher.execute(resources.getTtsCallbackUrl(), "", "", "true");
        Intent i = new Intent("ttsCompleted-event");
        LocalBroadcastManager.getInstance(myContext).sendBroadcast(i);
    }

    /**
     * Response received from tts callback receiver, just consume it..
     * @param result
     */
    public void resultReceived(String result)
    {
        Log.d(TAG, "Recevied result from TTS callback receiver: " + result);
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

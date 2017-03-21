package com.davan.alarmcontroller;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.davan.alarmcontroller.http.KeypadHttpService;
import com.davan.alarmcontroller.http.WifiConnectionChecker;
import com.davan.alarmcontroller.http.alarm.AlarmStateChecker;
import com.davan.alarmcontroller.http.services.TtsCreator;
import com.davan.alarmcontroller.http.services.TtsReader;
import com.davan.alarmcontroller.http.services.WakeUpScreen;
import com.davan.alarmcontroller.power.PowerConnectionReceiver;
import com.davan.alarmcontroller.settings.AlarmControllerResources;
import com.davan.alarmcontroller.settings.SettingsLauncher;
import com.davan.alarmcontroller.settings.UsageDialog;

import java.io.File;
import java.io.IOException;

public class TtsMode extends AppCompatActivity  {

    private static final String TAG = TtsMode.class.getSimpleName();
    private WifiConnectionChecker wifiChecker;
    private AlarmControllerResources resources;
    private String hostAddress;
    private static int receivedTtsRequests = 0;
    private static int receivedTtsFetchRequests = 0;
    private static int sentTtsCompletedRequests = 0;

    private TtsCreator ttsCreator = null;
    private TtsReader ttsReader = null;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received tts request");
            if (intent.getAction().equals("tts-event")) {
                TextView textView = (TextView) findViewById(R.id.receivedRequestsView);
                textView.setText(Integer.toString(++receivedTtsRequests));
            } else if(intent.getAction().equals("ttsFetch-event")) {
                TextView textView = (TextView) findViewById(R.id.receivedTtsFetchRequests);
                textView.setText(Integer.toString(++receivedTtsFetchRequests));
            } else if(intent.getAction().equals("ttsCompleted-event")) {
                TextView textView = (TextView) findViewById(R.id.sentRequestsView);
                textView.setText(Integer.toString(++sentTtsCompletedRequests));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_mode);

        resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(this),
                getSharedPreferences("com.davan.alarmcontroller.users", 0),
                getResources());
        startServices();

        loadSettings(resources);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");
        loadSettings(resources);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        stopServices();
    }

    /**
     *
     * @param resources
     */
    private void loadSettings(AlarmControllerResources resources)
    {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        hostAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        TextView textView = (TextView) findViewById(R.id.hostAddressView);
        textView.setText(hostAddress);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiChecker = new WifiConnectionChecker(connMgr);

        textView = (TextView) findViewById(R.id.wifiView);
        textView.setText(wifiChecker.isConnectionOk()?"Connected":"Disconnected");

        textView = (TextView) findViewById(R.id.callbackUrlView);
        textView.setText(resources.getTtsCallbackUrl());

        textView = (TextView) findViewById(R.id.httpServicesView);
        textView.setText(resources.isHttpServicesEnabled() ? "Enabled" : "Disabled");

        textView = (TextView) findViewById(R.id.ttsServiceView);
        textView.setText(resources.isTtsServiceEnabled() ? "Enabled" : "Disabled");

        textView = (TextView) findViewById(R.id.playOnDeviceView);
        textView.setText(resources.isTtsPlayOnDeviceEnabled()?"Enabled":"Disabled");


    }

    /**
     * Start services and register for events.
     */
    private void startServices()
    {
        Log.i(TAG, "Start services");
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("tts-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("ttsFetch-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("ttsCompleted-event"));


        if (resources.isHttpServicesEnabled())
        {
            startService(new Intent(getBaseContext(), KeypadHttpService.class));
        }
        ttsCreator = new TtsCreator(this, resources);
        ttsCreator.registerForEvents(this);
        ttsReader = new TtsReader(this, resources);
        ttsReader.registerForEvents(this);
    }

    /**
     * Stop services and unregister for events.
     */
    private void stopServices()
    {
        Log.i(TAG, "Stop services");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        if (ttsCreator !=null) {
            ttsCreator.unregisterForEvents(this);
        }
        if (ttsReader !=null) {
            ttsReader.unregisterForEvents(this);
        }
        stopService(new Intent(getBaseContext(), KeypadHttpService.class));
    }

    /**
     * Show the usage dialog
     * @param view
     */
    public void showUsageDialog(View view)
    {
        Log.d(TAG, "showUsageDialog");
        String usage = getResources().getString(R.string.desc_view);
        usage = usage.replace("<ipaddress>", hostAddress);
        usage = usage.replace("<callback_url>", resources.getTtsCallbackUrl());

        UsageDialog.showDialog(this, usage);
    }

    /**
     * Play the last generated speech in internal speaker
     * @param view
     */
    public void playLastGeneratedSpeech(View view)
    {
        Log.d(TAG, "playLastGeneratedSpeech");
        try {
            File mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(),
                    resources.getTtsStorageFolder());
            File mediaFile = new File(
                            mediaStorageDir.getPath() +
                            File.separator +
                            resources.getTtsFileName());

            Uri myUri = Uri.fromFile(mediaFile);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch(IOException e)
        {
            Log.w(TAG,"Failed to play media:" + e.getMessage());
        }
    }

    /**
     * Show settings
     * @param view
     */
    public void showSettings(View view)
    {
        Log.d(TAG, "showSettings");
        SettingsLauncher.verifySettingsPassword(this, resources);
    }
}

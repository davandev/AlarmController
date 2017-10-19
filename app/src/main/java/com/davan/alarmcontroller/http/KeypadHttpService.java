package com.davan.alarmcontroller.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.util.KeypadHttpRequestListener;
import com.davan.alarmcontroller.http.util.KeypadHttpServices;
import com.davan.alarmcontroller.http.util.WebServerControlListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

/**
 * Created by davandev on 2016-04-12.
 **/

public class KeypadHttpService extends Service implements KeypadHttpRequestListener, WebServerControlListener {
    private static final String TAG = KeypadHttpService.class.getSimpleName();

    private PowerManager.WakeLock mWakeLock = null;
    private AlarmControllerResources resources;

    private KeypadHttpServer webServer;
    private KeypadHttpServices httpServices;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "bind");
        Toast.makeText(this, "Http service bind", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "starting");

        resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(this),
                getSharedPreferences("com.davan.alarmcontroller.users", 0),
                getResources());

        Toast.makeText(this, "Starting http service", Toast.LENGTH_LONG).show();
        try {
            webServer = new KeypadHttpServer(this);
            webServer.start();

            httpServices = new KeypadHttpServices(resources);
            httpServices.createServices(this, (WebServerControlListener)this);

        } catch (IOException ioe) {
            Log.d(TAG, "Couldn't start server" + ioe);
            Toast.makeText(this, "Failed to start http service :" + ioe.getMessage(), Toast.LENGTH_LONG).show();
            return START_STICKY;
        }
        Log.d(TAG, "Http service listening on port 8080");
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Toast.makeText(this, "Http service Listening on " + ip + ":8080", Toast.LENGTH_LONG).show();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("ZenitGatekeeper http service");
        mBuilder.setContentText("Started on " + ip + ":8080");
        mBuilder.setOngoing(true);
        Notification not = mBuilder.build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1123, not);

        startForeground(1123, not);
        mWakeLock.acquire();

        return START_STICKY;
        //return
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroyed");
        webServer.stop();
        httpServices.destroyServices(this);
        Toast.makeText(this, "Http service service destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    /**
     * Wakeup request received, check if service is enabled and notify receivers.
     *
     * @return true if enabled, false otherwise
     */
    public boolean wakeup() {
        if (resources.isWakeUpServiceEnabled()) {
            Log.d(TAG, "wakeup request received");
            Intent i = new Intent("wakeup-event");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            return true;
        }
        return false;
    }

    /**
     * TTS request received, check if service is enabled and notify receivers.
     *
     * @param message message to speak
     * @return true if enabled, false otherwise
     */
    public boolean tts(String message) {
        if (resources.isTtsServiceEnabled()) {
            Log.d(TAG, "tts request received");
            Intent i = new Intent("tts-event");
            i.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            return true;
        }
        return false;
    }

    /**
     * Returns the generated speech media file
     *
     * @return Response the generated media file
     */
    public NanoHTTPD.Response getSpeechFile() {
        if (resources.isTtsServiceEnabled()) {
            Log.d(TAG, "ttsFetch request received");
            Intent i = new Intent("ttsFetch-event");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);

            String TTS_DIRECTORY_NAME = "GeneratedTTS";
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), TTS_DIRECTORY_NAME);
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "TTS.wav");
            Log.d(TAG, "Return Ttsfile: " + mediaFile.getAbsolutePath());

            try {
                FileInputStream fis = new FileInputStream(mediaFile);
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.OK,
                        "audio/mpeg",
                        fis,
                        mediaFile.length());
            } catch (IOException e) {
                Log.d(TAG, "Failed to return Ttsfile");
            }

        }
        return NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.INTERNAL_ERROR,
                "text/html", "Failed to return file");
    }

    /**
     * Log file request received
     *
     * @return reponse message
     */
    public NanoHTTPD.Response getLogFile() {
        Log.d(TAG, "/log request received");
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "<br>");
            }
            return NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.OK,
                    "text/html",
                    log.toString());
        } catch (IOException e) {
            Log.d(TAG, "Failed to produce logfile");
        }
        return NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.INTERNAL_ERROR,
                "text/html", "Failed to return log file");

    }

    /**
     * Play request received
     *
     * @param message, local path to media file
     * @return true
     */
    public boolean play(String message) {
        Log.d(TAG, "play request received");
        Intent i = new Intent("play-event");
        i.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        return true;
    }

    public void pingReceived() {
        Log.d(TAG, "Ping request received");
        Intent i = new Intent("ping-event");
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    @Override
    public void restartWebServer() {
        Log.d(TAG, "Restart webserver");
        try {
            webServer.stop();
            webServer.start();
        } catch (IOException ioe) {
            Log.d(TAG, "Couldn't start server" + ioe);
        }
    }
}
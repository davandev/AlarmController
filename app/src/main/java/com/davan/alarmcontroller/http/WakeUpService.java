package com.davan.alarmcontroller.http;
/**
 * Created by davandev on 2016-04-12.
 **/

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.util.WakeUpReceiver;

public class WakeUpService extends Service implements WakeUpReceiver
{
    private static final String TAG = WakeUpService.class.getName();

    private PowerManager.WakeLock mWakeLock = null;
    WakeUpServer webServer;

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "WakeUpService bind");
        Toast.makeText(this, "service bind", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent,flags,startId);

        Log.d(TAG, "WakeUpService starting");
        Toast.makeText(this, "WakeUp service starting", Toast.LENGTH_LONG).show();
        try
        {
            webServer = new WakeUpServer(this);
            webServer.start();
        }
        catch( IOException ioe )
        {
            Log.d(TAG, "WakeUpService Couldn't start server" + ioe);
            Toast.makeText(this, "Failed to start WakeUpService :" + ioe.getMessage(), Toast.LENGTH_LONG).show();
            return START_STICKY;
        }
        Log.d(TAG, "WakeUpService Listening on port 8080");
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Toast.makeText(this, "WakeUpService Listening on "+ ip +":8080", Toast.LENGTH_LONG).show();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("AlarmController WakeUp Service");
        mBuilder.setContentText("Started on "+ip+":8080");
        mBuilder.setOngoing(true);
        Notification not = mBuilder.build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1123, not);

        startForeground(1123,not);
        mWakeLock.acquire();

        return START_STICKY;
        //return
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "WakeUpService destroyed");
        webServer.stop();
        Toast.makeText(this, "WakeUp service destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate");
        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    public void wakeup()
    {
        Log.d(TAG,"callback received");
        Intent i = new Intent("wakeup-event");
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}

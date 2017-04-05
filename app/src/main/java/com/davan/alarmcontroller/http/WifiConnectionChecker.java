package com.davan.alarmcontroller.http;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by davandev on 2016-03-24.
 **/
public class WifiConnectionChecker
{
    private static final String TAG = WifiConnectionChecker.class.getSimpleName();

    private final ConnectivityManager connMgr;
    public WifiConnectionChecker(ConnectivityManager connectivityManager)
    {
        connMgr = connectivityManager;
    }

    public boolean isConnectionOk()
    {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
        {
            Log.d(TAG,"Wifi connection is available");
            return true;
        }
        else
        {
            Log.d(TAG, "No wifi connection available");
            return false;
        }
    }
}


package com.davan.alarmcontroller.http;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by davandev on 2016-03-24.
 */
public class WifiConnectionChecker
{
    private final static String CLASSNAME = "WifiConnectionChecker";

    private ConnectivityManager connMgr;
    public WifiConnectionChecker(ConnectivityManager connectivityManager)
    {
        connMgr = connectivityManager;
    }

    public boolean isConnectionOk()
    {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            Log.d(CLASSNAME,"Network connection is available");
            return true;
        }
        else
        {
            Log.d(CLASSNAME, "No network connection available");
            return false;
        }
    }
}


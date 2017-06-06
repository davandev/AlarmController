package com.davan.alarmcontroller.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.content.Context.WIFI_SERVICE;

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
        if (networkInfo != null &&
            networkInfo.isConnected() &&
           (networkInfo.getType() == ConnectivityManager.TYPE_WIFI))
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

    public String getHostAddress(Context context)
    {
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo ethernet = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        if(wifi.isAvailable() && wifi.isConnected())
        {
            Log.d(TAG, "Using wifi");

            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        }
        else if (ethernet.isAvailable() && ethernet.isConnected())
        {
            Log.d(TAG, "Using ethernet");
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {
                return "ERROR Obtaining IP";
            }
        }
        return "No IP Available";
    }
}


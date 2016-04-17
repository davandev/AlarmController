package com.davan.alarmcontroller.settings;
/**
 * Created by davandev on 2016-04-12.
 */
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlarmControllerResources
{
    private static final String TAG = AlarmControllerResources.class.getSimpleName();

    private SharedPreferences preferences;
    private SharedPreferences userPreferences;
    private Resources resources;
    private HashMap<String,String> users = new HashMap<String,String>();
    private String defaultUser;
    private String defaultUserPassword;

    public AlarmControllerResources(SharedPreferences pref, SharedPreferences userPref, Resources res)
    {
        preferences = pref;
        userPreferences = userPref;
        resources = res;

        findDefaultUser();
    }

    public String getFibaroServerAddress() { return preferences.getString("fibaro_auth_server_address",""); }
    public String getDefaultUser() { return defaultUser; }
    public String getDefaultPassword()
    {
        return defaultUserPassword;
    }
    public String getSettingsPassword() { return preferences.getString("settings_protection","1234");}

    public boolean isFibaroServerEnabled() { return preferences.getBoolean("fibaro_server_enabled", false); }
    public boolean isExternalServerEnabled() { return preferences.getBoolean("ext_server_enabled",false); }

    public Resources getResources() { return resources; }
    public SharedPreferences getPreferences()
    {
        return preferences;
    }

    public Pair<String,String> getUser(String pin) throws Exception
    {
        Log.d(TAG,"getUser: "+ pin);
        for( Map.Entry entry : userPreferences.getAll().entrySet() )
        {
            Log.d(TAG,"User:"+entry.getKey().toString()+ " Value:"+ entry.getValue().toString());
            String[] credentials = entry.getValue().toString().split(":");
            if(pin.compareTo(credentials[1]) == 0)
            {
                Log.d(TAG,"Found user:" + entry.getKey().toString() + " password: " + credentials[1]);
                return new Pair<String,String>(entry.getKey().toString(),credentials[0]);
            }

        }
        throw new Exception("User not found");
    }

    private void findDefaultUser()
    {
        for( Map.Entry entry : userPreferences.getAll().entrySet() )
        {
            users.put(entry.getKey().toString(), entry.getValue().toString());
        }
        Iterator it = users.keySet().iterator();
        while (it.hasNext())
        {
            String user = (String)it.next();
            String value = users.get(user);
            Log.d(TAG,"User:"+ user+ " Value:" + value);
            String[] userSettings = value.split(":");
            if ( Boolean.parseBoolean(userSettings[2]) == true)
            {
                defaultUser = user;
                defaultUserPassword = userSettings[0];
                break;
            }
        }
    }

}

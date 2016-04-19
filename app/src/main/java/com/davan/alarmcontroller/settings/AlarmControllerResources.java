package com.davan.alarmcontroller.settings;
/**
 * Created by davandev on 2016-04-12.
 **/
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
    private HashMap<String,String> users = new HashMap<>();
    private String defaultUser;
    private String defaultUserPassword;

    public AlarmControllerResources(SharedPreferences pref, SharedPreferences userPref, Resources res)
    {
        preferences = pref;
        userPreferences = userPref;
        resources = res;

        findDefaultUser();
    }

    /* Return the configured fibaro server address */
    public String getFibaroServerAddress() { return preferences.getString("fibaro_auth_server_address",""); }
    /* Return the default user */
    public String getDefaultUser() { return defaultUser; }
    /* Return the password of the default user*/
    public String getDefaultPassword() { return defaultUserPassword; }
    public String getSettingsPassword() { return preferences.getString("settings_protection", "1234");}

    public boolean isFibaroServerEnabled() { return preferences.getBoolean("fibaro_server_enabled", false); }
    public boolean isExternalServerEnabled() { return preferences.getBoolean("ext_server_enabled",false); }
    public boolean isWakeUpServiceEnabled() {return preferences.getBoolean("wake_up_service_enabled",false);}
    public Resources getResources() { return resources; }
    public SharedPreferences getPreferences()
    {
        return preferences;
    }

    public Pair<String,String> getUser(String pin) throws Exception
    {
        Log.d(TAG,"getUser with pin[" + pin + "]");
        for( Map.Entry entry : userPreferences.getAll().entrySet() )
        {
            String[] credentials = entry.getValue().toString().split(":");
            if(pin.compareTo(credentials[1]) == 0)
            {
                Log.d(TAG,"Found user:" + entry.getKey().toString());
                return new Pair<>(entry.getKey().toString(),credentials[0]);
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
            if (userSettings.length == 3) {
                if (Boolean.parseBoolean(userSettings[2])) {
                    defaultUser = user;
                    defaultUserPassword = userSettings[0];
                    break;
                }
            }
        }
    }

    public void verifyConfiguration() throws Exception
    {
        if (getDefaultUser() == null || getDefaultUser().compareTo("") == 0)
        {
            throw new Exception("Default user not configured");
        }
        if (getDefaultPassword() == null || getDefaultPassword().compareTo("") == 0)
        {
            throw new Exception("Password for default user not configured");
        }
        if (getFibaroServerAddress().compareTo("") == 0)
        {
            throw new Exception("Fibaro server address not configured");
        }
        if (!isFibaroServerEnabled() && !isExternalServerEnabled())
        {
            throw new Exception("Either Fibaro server procedure or External server procedure needs to be configured.");
        }
    }
}

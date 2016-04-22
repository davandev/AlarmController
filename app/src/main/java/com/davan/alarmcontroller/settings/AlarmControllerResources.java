package com.davan.alarmcontroller.settings;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;

import com.davan.alarmcontroller.R;

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
    /* Return the password protecting settings menu*/
    public String getSettingsPassword() { return preferences.getString("settings_protection", "1234");}

    public boolean isFibaroServerEnabled() { return preferences.getBoolean("fibaro_server_enabled", false); }
    public boolean isExternalServerEnabled() { return preferences.getBoolean("ext_server_enabled", false); }
    public boolean isWakeUpServiceEnabled() {return preferences.getBoolean("wake_up_service_enabled", false);}
    public Resources getResources() { return resources; }
    public SharedPreferences getPreferences() { return preferences; }
    public String getFibaroAlarmStateVariableName() { return preferences.getString("fibaro_variable_alarmstate", "AlarmState");}
    public String getFibaroAlarmStateValueDisarmed() { return preferences.getString("fibaro_variable_alarmstate_disarmed", "Disarmed");}
    public String getFibaroAlarmStateValueArmed() { return preferences.getString("fibaro_variable_alarmstate_armed", "Armed");}

    public String getFibaroAlarmTypeVariableName() { return preferences.getString("fibaro_variable_alarmtype", "AlarmType");}
    public String getFibaroAlarmTypeValueFullHouseArmed() { return preferences.getString("fibaro_variable_alarmtype_fullhouse", "Alarm");}
    public String getFibaroAlarmTypeValuePerimeterArmed() { return preferences.getString("fibaro_variable_alarmtype_perimeter", "Perimeter");}

    public String getTelegramToken() { return preferences.getString("telegram_token","");}
    public String getTelegramChatId() { return preferences.getString("telegram_chat_id","");}
    public boolean isTelegramEnabled() { return preferences.getBoolean("telegram_enabled", false);}

    public String getTelegramSendMessageUrl(String token, String chatId)
    {
        String url = resources.getString(R.string.pref_url_telegram_send_message);
        url = url.replace("<token>",token);
        url = url.replace("<chatId>",chatId);
        return url;
    }
    /* Return the fibaro user and password matching the pin code
    * Throws exception if no matching user is found */
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

    /**
     * Iterate through all users in search for the default user.
     */
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
        if (isFibaroServerEnabled() && isExternalServerEnabled())
        {
            throw new Exception("Both Fibaro server procedure or External server procedure is currently configured.");
        }
    }

    public boolean isPictureAtDisarmEnabled()
    {
        return preferences.getBoolean("take_picture_enabled",false);
    }
}

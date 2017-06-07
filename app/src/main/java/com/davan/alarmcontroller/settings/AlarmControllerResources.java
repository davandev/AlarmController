package com.davan.alarmcontroller.settings;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;

import com.davan.alarmcontroller.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to access preferences, strings, configuration
 */
public class AlarmControllerResources
{
    private static final String TAG = AlarmControllerResources.class.getSimpleName();

    private final SharedPreferences preferences;
    private final SharedPreferences userPreferences;
    private final Resources resources;
    private final HashMap<String,String> users = new HashMap<>();
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
    /* Return the configured escape time */
    public String getEscapingTime() { return preferences.getString("escaping_time", "30");}

    public boolean isFibaroServerEnabled() { return preferences.getBoolean("fibaro_server_enabled", false); }
    public boolean isExternalServerEnabled() { return preferences.getBoolean("ext_server_enabled", false); }
    /* Return the configured url to receiver of tts callbacks */
    public String getTtsCallbackUrl() { return preferences.getString("tts_callback_url", "");}
    public String getTtsStorageFolder() {return resources.getString(R.string.text_tts_folder_name);}
    public String getTtsFileName() {return resources.getString(R.string.text_tts_file_name);}
    public String getTtsSpeechRate() {return preferences.getString("tts_speech_speed","1.0");}

    /* Return true if http services is enabled */
    public boolean isHttpServicesEnabled() {return preferences.getBoolean("http_service_enabled", false);}
    /* Return true if wakeup service is enabled*/
    public boolean isWakeUpServiceEnabled() {return preferences.getBoolean("wakeup_service_enabled", false);}
    /* Return true if tts service is enabled*/
    public boolean isTtsServiceEnabled() {return preferences.getBoolean("tts_service_enabled", false);}
    /* Return true if tts should be played on device */
    public boolean isTtsPlayOnDeviceEnabled() {return preferences.getBoolean("tts_play_on_device_enabled", false);}
    public boolean isPlayAnnouncementOnDeviceEnabled() {return preferences.getBoolean("play_announcement_enabled", false);}
    public String getAnnouncementFile() {return preferences.getString("announcement_file", "");}

    public Resources getResources() { return resources; }
    public SharedPreferences getPreferences() { return preferences; }
    /* Return the configured Fibaro variable for AlarmState*/
    public String getFibaroAlarmStateVariableName() { return preferences.getString("fibaro_variable_alarmstate", "AlarmState");}
    /* Return the configured Fibaro variable when alarmstate is disarmed*/
    public String getFibaroAlarmStateValueDisarmed() { return preferences.getString("fibaro_variable_alarmstate_disarmed", "Disarmed");}
    /* Return the configured Fibaro variable when alarmstate is disarmed*/
    public String getFibaroAlarmStateValueBreached() { return preferences.getString("fibaro_variable_alarmstate_breached", "Breached");}
    /* Return the configured Fibaro variable when alarmstate is armed*/
    public String getFibaroAlarmStateValueArmed() { return preferences.getString("fibaro_variable_alarmstate_armed", "Armed");}
    /* Return the configured Fibaro variable for alarmtype*/
    public String getFibaroAlarmTypeVariableName() { return preferences.getString("fibaro_variable_alarmtype", "AlarmType");}
    public String getFibaroAlarmTypeValueFullHouseArmed() { return preferences.getString("fibaro_variable_alarmtype_fullhouse", "Alarm");}
    public String getFibaroAlarmTypeValuePerimeterArmed() { return preferences.getString("fibaro_variable_alarmtype_perimeter", "Perimeter");}

    /* Return the configured telegram token*/
    public String getTelegramToken() { return preferences.getString("telegram_token", "");}

    public boolean isTelegramEnabled() { return preferences.getBoolean("telegram_enabled", false);}
    /* Return true if charging control is enabled in configuration*/
    public boolean isChargingControlEnabled() { return preferences.getBoolean("charging_enabled", false);}
    /* Return the scene id to invoke when charging should be turned on. */
    public String getTurnOnChargingSceneId() {return preferences.getString("battery_turn_on_charging", "");}
    /* Return the scene id to invoke when charging should be turned off. */
    public String getTurnOffChargingSceneId() {return preferences.getString("battery_turn_off_charging", "");}

    public String getFibaroRunSceneUrl() {
        return resources.getString(R.string.pref_fibaro_server_run_scene_url);
    }
    public String getFibaroRunVirtualDeviceUrl()
    {
        return resources.getString(R.string.pref_fibaro_server_run_virtual_device_url);
    }
    public String getVirtualDeviceId()
    {
        return preferences.getString("virtual_device_id", "");
    }

    public String getKeypadId() {return preferences.getString("keypad_id", "");}
    /* Return the telegram url with token and chatid */
    public String getTelegramSendMessageUrl(String token, String chatId)
    {
        String url = resources.getString(R.string.pref_url_telegram_send_message);
        url = url.replace("<token>",token);
        url = url.replace("<chatId>",chatId);
        return url;
    }

    /* Return a list of all the users chatids */
    public ArrayList<String> getAllTelegramChatIds()
    {
        ArrayList<String> chatIds = new ArrayList<>();
        for( Map.Entry entry : userPreferences.getAll().entrySet() )
        {
            String[] credentials = entry.getValue().toString().split(":");
            if(credentials[2].compareTo("")!= 0)
            {
                Log.d(TAG,"Found chatID:" + credentials[2]);
                chatIds.add(credentials[2]);
            }
        }
        return chatIds;
    }

    /* Return the fibaro user and password matching the pin code
    * Throws exception if no matching user is found */
    public Pair<String,String> getUser(String pin) throws Exception
    {
        Log.d(TAG,"getUser with pin[" + pin + "]");
        for( Map.Entry entry : userPreferences.getAll().entrySet() )
        {
            String[] credentials = entry.getValue().toString().split(":");
            Log.d(TAG, "user:" + entry.getKey().toString() + " " + credentials[1]);
            if(pin.compareTo(credentials[1]) == 0)
            {
                Log.d(TAG, "Found user:" + entry.getKey().toString());
                return new Pair<>(entry.getKey().toString(),credentials[0]);
            }
        }
        Log.d(TAG,"No user with that pin");

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
        for (Object user : users.keySet())
        {
            String value = users.get(user);
            String[] userSettings = value.split(":");
            if (userSettings.length == 4) {
                if (Boolean.parseBoolean(userSettings[3])) {
                    defaultUser = (String)user;
                    defaultUserPassword = userSettings[0];
                    break;
                }
            }
        }
    }
    public void verifyTtsConfiguration() throws Exception
    {
        if(!isHttpServicesEnabled()) {
            throw new Exception("Http services is not enabled.");
        }
        if(isTtsServiceEnabled()) {
            throw new Exception("TTS service is not enabled");
        }

        if(getTtsCallbackUrl().compareTo("") == 0)
        {
                    throw new Exception(resources.getString(R.string.pref_message_no_callback_url_configured));
        }



    }
    /**
     * Verify that configuration is ok
     * @throws Exception when configuration is faulty.
     */
    public void verifyConfiguration() throws Exception
    {
        findDefaultUser();
        if (getDefaultUser() == null || getDefaultUser().compareTo("") == 0)
        {
            throw new Exception(resources.getString(R.string.pref_message_default_user_not_configured));
        }
        if (getDefaultPassword() == null || getDefaultPassword().compareTo("") == 0)
        {
            throw new Exception(resources.getString(R.string.pref_message_password_not_configured));
        }
        if (getFibaroServerAddress().compareTo("") == 0)
        {
            throw new Exception(resources.getString(R.string.pref_message_fibaro_server_address_not_configured));
        }
        if (!isFibaroServerEnabled() && !isExternalServerEnabled())
        {
            throw new Exception(resources.getString(R.string.pref_message_no_procedure_server_configured));
        }
        if (isFibaroServerEnabled() && isExternalServerEnabled())
        {
            throw new Exception(resources.getString(R.string.pref_message_both_fibaro_and_external_server_configured));
        }
        if (isTelegramEnabled())
        {
            if (getAllTelegramChatIds().size() == 0)
            {
                throw new Exception(resources.getString(R.string.pref_message_no_telegram_chat_id_configured));
            }
            if(getTelegramToken().compareTo("") == 0)
            {
                throw new Exception(resources.getString(R.string.pref_message_not_token_configured));
            }
        }
        if (isChargingControlEnabled())
        {
            if (getTurnOnChargingSceneId().compareTo("") == 0)
            {
                throw new Exception(resources.getString(R.string.pref_message_no_charging_turn_on_configured));
            }
            if (getTurnOffChargingSceneId().compareTo("") == 0)
            {
                throw new Exception(resources.getString(R.string.pref_message_no_charging_turn_off_configured));
            }
        }

        if(isHttpServicesEnabled())
        {
            if(isTtsServiceEnabled())
            {
                if(getTtsCallbackUrl().compareTo("") == 0)
                {
                    throw new Exception(resources.getString(R.string.pref_message_no_callback_url_configured));
                }
            }
        }
    }

    /**
     * Determine if camera should take a picture when alarm is disarmed.
     * @return true if enabled
     */
    public boolean isPictureAtDisarmEnabled()
    {
        return preferences.getBoolean("take_picture_enabled",false);
    }
}

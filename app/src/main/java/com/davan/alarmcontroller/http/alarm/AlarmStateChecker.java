package com.davan.alarmcontroller.http.alarm;

import android.util.Log;

import com.davan.alarmcontroller.http.RequestDispatcher;
import com.davan.alarmcontroller.http.RequestDispatcherResultListener;
import com.davan.alarmcontroller.http.WifiConnectionChecker;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davandev on 2016-03-26.
 */
public class AlarmStateChecker implements RequestDispatcherResultListener
{
    private static final String TAG = AlarmStateChecker.class.getName();

    RequestDispatcher requestSender;
    WifiConnectionChecker wifiChecker;
    AlarmStateListener resultReceiver;
    private String serverUrl = "";
    private AlarmControllerResources resources;

    public AlarmStateChecker(WifiConnectionChecker wifiCheckerInstance,
                             AlarmControllerResources alarmControllerResources,
                             AlarmStateListener alarmListener)
    {
        wifiChecker = wifiCheckerInstance;
        resultReceiver = alarmListener;
        requestSender = new RequestDispatcher(this);
        resources = alarmControllerResources;
        serverUrl = "http://" + alarmControllerResources.getFibaroServerAddress()+ "/api/globalVariables";

    }

    public boolean updateAlarmState()
    {
        if( wifiChecker.isConnectionOk() ) // Check wifi
        {
            //
            requestSender.execute(
                    serverUrl,
                    resources.getDefaultUser(),
                    resources.getDefaultPassword(),
                    Boolean.toString(true));
            return true;
        }
        return false;
    }

    @Override
    public void resultReceived(String result)
    {
        Log.d(TAG, "resultReceived, result:" + result);
        try
        {
            String alarmState = "";
            String alarmType ="";

            result = result.replace("200","");
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("name").compareTo("AlarmState") == 0)
                {
                    alarmState = jsonObject.getString("value");
                }
                else if (jsonObject.getString("name").compareTo("AlarmType") == 0)
                {
                    alarmType = jsonObject.getString("value");
                }
            }
            Log.d(TAG, "AlarmType[" + alarmType + "] AlarmState[" + alarmState + "]");

            resultReceiver.alarmStateUpdate(alarmState,alarmType);
        }
        catch (JSONException e)
        {
            Log.d(TAG, "Failed to parse alarm state response");
            e.printStackTrace();
        }
    }
}

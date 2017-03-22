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
 **/
public class AlarmStateChecker implements RequestDispatcherResultListener
{
    private static final String TAG = AlarmStateChecker.class.getName();

    private final RequestDispatcher requestSender;
    private final WifiConnectionChecker wifiChecker;
    // Receiver of alarm state
    private final AlarmStateListener resultReceiver;
    private final AlarmControllerResources resources;

    private String serverUrl = "";

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

    /**
     * Fetch alarm status from Fibaro system
     * @return true if wifi connection is available, false otherwise.
     */
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

    /**
     * Callback received with current alarm state from Fibaro system.
     * @param result Result
     */
    @Override
    public void resultReceived(String result)
    {
        //Log.d(TAG, "resultReceived, result:" + result);
        try
        {
            String alarmState = "";
            String alarmType ="";

            result = result.replace("200","");
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("name").compareTo(resources.getFibaroAlarmStateVariableName()) == 0)
                {
                    alarmState = jsonObject.getString("value");
                }
                else if (jsonObject.getString("name").compareTo(resources.getFibaroAlarmTypeVariableName()) == 0)
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

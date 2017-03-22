package com.davan.alarmcontroller.procedures;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.util.Log;
import android.util.Pair;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.RequestDispatcher;
import com.davan.alarmcontroller.http.RequestDispatcherResultListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;


public class FibaroServerAlarmProcedure implements AlarmProcedureIf, RequestDispatcherResultListener
{
    private static final String TAG = FibaroServerAlarmProcedure.class.getSimpleName();

    private final RequestDispatcher requestDispatcher;
    private final AlarmProcedureResultListener resultListener;
    private final AlarmControllerResources resources;
    private String action;
    private String url = "";
    private String authenticatedUser ="";

    public FibaroServerAlarmProcedure(
            AlarmControllerResources alarmControllerResources,
            AlarmProcedureResultListener listener)
    {
        resources = alarmControllerResources;
        resultListener = listener;
        requestDispatcher = new RequestDispatcher(this);
        url = resources.getFibaroRunVirtualDeviceUrl();
        url = url.replace("<serveraddress>", resources.getFibaroServerAddress());
        url = url.replace("<id>", resources.getVirtualDeviceId());
    }

    /**
     * Perform an arm attempt towards the Fibaro server.
     * @param alarmType
     */
    @Override
    public void arm(String alarmType)
    {
        action = resources.getResources().getString(R.string.arming);

        if (alarmType.compareTo(resources.getResources().getString(R.string.alarm_type_skalskydd)) == 0)
        {
            url = url.replace("<buttonId>", resources.getPreferences().getString("arm_shell_button_id", ""));
        }
        else
        {
            url = url.replace("<buttonId>", resources.getPreferences().getString("arm_alarm_button_id", ""));
        }
        Log.d(TAG, "Arming url: " + url);
        requestDispatcher.execute(url,
                resources.getDefaultUser(),
                resources.getDefaultPassword(),
                Boolean.toString(true));
    }

    /**
     * Perform a disarm attempt towards Fibaro server.
     * @param alarmType
     * @param pin, user pin code
     */
    @Override
    public void disarm(String alarmType, String pin)
    {
        try {
            // Get the user corresponding to the pincode
            Pair<String, String> user = resources.getUser(pin);
            // Store the user, to feedback on screen at successful disarming.
            authenticatedUser = user.first;
            action = resources.getResources().getString(R.string.disarming);
            if (alarmType.compareTo(resources.getResources().getString(R.string.alarm_type_skalskydd)) == 0) {
                url = url.replace("<buttonId>", resources.getPreferences().getString("disarm_shell_button_id", ""));
            } else {
                url = url.replace("<buttonId>", resources.getPreferences().getString("disarm_alarm_button_id", ""));
            }
            Log.d(TAG, "Disarming url: " + url);
            requestDispatcher.execute(url, user.first, user.second, Boolean.toString(true));
        }
        catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
            resultListener.resultReceived(false,"");
        }
    }

    /**
     * Callback from RequestDispatcher with result from server.
     * @param result
     */
    @Override
    public void resultReceived(String result)
    {
        Log.d(TAG,"Result: "+ result);
        resultListener.resultReceived(true,authenticatedUser);
    }
}

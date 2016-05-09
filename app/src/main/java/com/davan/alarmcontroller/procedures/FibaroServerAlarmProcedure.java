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

    private RequestDispatcher requestDispatcher;
    private AlarmProcedureResultListener resultListener;
    private String url = "";
    private AlarmControllerResources resources;
    private String action;
    private String authenticatedUser ="";

    public FibaroServerAlarmProcedure(
            AlarmControllerResources alarmControllerResources,
            AlarmProcedureResultListener listener)
    {
        resources = alarmControllerResources;
        resultListener = listener;
        requestDispatcher = new RequestDispatcher(this);
        url = resources.getFibaroRunSceneUrl();
        url = url.replace("<serveraddress>", resources.getFibaroServerAddress());

    }
    @Override
    public void arm(String alarmType)
    {
        action = resources.getResources().getString(R.string.arming);

        if (alarmType.compareTo(resources.getResources().getString(R.string.alarm_type_skalskydd)) == 0)
        {
            url = url.replace("<id>", resources.getPreferences().getString("arm_shell_scene", ""));
        }
        else
        {
            url = url.replace("<id>", resources.getPreferences().getString("arm_alarm_scene", ""));
        }
        Log.d(TAG, "Arming url: " + url);
        requestDispatcher.execute(url,
                resources.getDefaultUser(),
                resources.getDefaultPassword(),
                Boolean.toString(true));
    }

    @Override
    public void disarm(String alarmType, String pin)
    {
        try {
            Pair<String, String> user = resources.getUser(pin);
            authenticatedUser = user.first;
            action = resources.getResources().getString(R.string.disarming);
            if (alarmType.compareTo(resources.getResources().getString(R.string.alarm_type_skalskydd)) == 0) {
                url = url.replace("<id>", resources.getPreferences().getString("disarm_shell_scene", ""));
            } else {
                url = url.replace("<id>", resources.getPreferences().getString("disarm_alarm_scene", ""));
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

    @Override
    public void resultReceived(String result)
    {
        Log.d(TAG,"Result: "+ result);
        resultListener.resultReceived(true,authenticatedUser);
    }
}

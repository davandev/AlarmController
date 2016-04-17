package com.davan.alarmcontroller.authentication;
/**
 * Created by davandev on 2016-04-12.
 */
import android.util.Log;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.RequestDispatcher;
import com.davan.alarmcontroller.http.RequestDispatcherResultListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

import org.json.JSONException;
import org.json.JSONObject;

public class ExternalServerAuthenticationHandler implements AuthenticationHandlerIf, RequestDispatcherResultListener
{
    private static final String TAG = ExternalServerAuthenticationHandler.class.getSimpleName();

    private RequestDispatcher urlFetcher;
    private AuthenticationResultListener resultListener;
    private AlarmControllerResources resources;
    private String action = "";

    public ExternalServerAuthenticationHandler(AlarmControllerResources alarmControllerResources,
                                               AuthenticationResultListener listener)
    {
        resources = alarmControllerResources;
        resultListener = listener;


        urlFetcher = new RequestDispatcher(this);
    }

    @Override
    public void arm(String alarmType)
    {
        Log.d(TAG, "Arm");
        action = resources.getResources().getString(R.string.arming);
        String arm_scene_url ="";
        String server_address = resources.getPreferences().getString("ext_auth_server_address", "");
        if (alarmType.compareTo(resources.getResources().getString(R.string.alarm_type_skalskydd)) == 0)
        {
            arm_scene_url = resources.getPreferences().getString("ext_arm_shell_url", "");
        }
        else
        {
            arm_scene_url = resources.getPreferences().getString("ext_arm_alarm_url", "");
        }
        String arm_url = "http://" +server_address + "/" + arm_scene_url;
        Log.d(TAG,"Arming url: "+arm_url );
        urlFetcher.execute(arm_url,"","",Boolean.toString(true));
    }

    @Override
    public void disarm(String alarmType,String pin)
    {
        Log.d(TAG,"Disarm" );
        action = resources.getResources().getString(R.string.disarming);

        String disarm_scene_url ="";
        String server_address = resources.getPreferences().getString("ext_auth_server_address", "");
        if (alarmType.compareTo(resources.getResources().getString(R.string.alarm_type_skalskydd)) == 0)
        {
            disarm_scene_url = resources.getPreferences().getString("ext_disarm_shell_url", "");
        }
        else
        {
            disarm_scene_url = resources.getPreferences().getString("ext_disarm_alarm_url", "");
        }
        String disarm_url = "http://" + server_address + "/" + disarm_scene_url  + pin;

        Log.d(TAG, "Disarm url: "+disarm_url);
        urlFetcher.execute(disarm_url,"","",Boolean.toString(true));
    }

    @Override
    public void resultReceived(String result)
    {
        if (result.contains("200"))
        {
            if (action.compareTo(resources.getResources().getString(R.string.arming))== 0)
            {
                resultListener.resultReceived(true, "");
            }
            else
            {
                result = result.replace("200","");
                String user ="";
                try
                {
                    JSONObject jObject = new JSONObject(result);
                    user= jObject.getString("user");
                    resultListener.resultReceived(true , user);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            resultListener.resultReceived(false, "");
        }
    }
}

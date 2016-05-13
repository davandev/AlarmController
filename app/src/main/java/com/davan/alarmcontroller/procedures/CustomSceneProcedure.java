package com.davan.alarmcontroller.procedures;

import android.util.Log;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.http.RequestDispatcher;
import com.davan.alarmcontroller.http.RequestDispatcherResultListener;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

/**
 * Created by davandev on 2016-05-08.
 **/
public class CustomSceneProcedure  implements RequestDispatcherResultListener
{
    private static final String TAG = CustomSceneProcedure.class.getSimpleName();

    private RequestDispatcher requestSender;
    private CustomSceneProcedureResultListener resultListener;
    private AlarmControllerResources resources;
    private String url = "";
    public CustomSceneProcedure(AlarmControllerResources alarmControllerResources,
                                CustomSceneProcedureResultListener listener)
    {
        resources = alarmControllerResources;
        resultListener = listener;
        requestSender = new RequestDispatcher(this);
    }

    /**
     * Invoke a scene on fibaro system.
     * @param id scene id
     */
    public void runScene( String id)
    {
        Log.d(TAG, "runScene");
        url = resources.getFibaroRunSceneUrl();
        url = url.replace("<serveraddress>", resources.getFibaroServerAddress());
        url = url.replace("<id>", id);
        Log.d(TAG, "Execute: " + url);
        requestSender.execute(url,
                resources.getDefaultUser(),
                resources.getDefaultPassword(),
                Boolean.toString(true));

    }

    @Override
    public void resultReceived(String result)
    {
        Log.d(TAG, "Result: " + result);
    }
}

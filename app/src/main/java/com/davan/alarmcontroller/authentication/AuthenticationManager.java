package com.davan.alarmcontroller.authentication;
/**
 * Created by davandev on 2016-04-12.
 */
import android.util.Log;

import com.davan.alarmcontroller.settings.AlarmControllerResources;

public class AuthenticationManager
{
    private static final String TAG = AuthenticationManager.class.getSimpleName();

    public static AuthenticationHandlerIf createHandler(
                                                        AlarmControllerResources alarmControllerResources,
                                                        AuthenticationResultListener listener) throws Exception
    {
        Log.d(TAG, "CreateHandler");
        if (alarmControllerResources.isFibaroServerEnabled())
        {
            Log.d(TAG,"Fibaro server is enabled");
            return new FibaroServerAuthenticationHandler(alarmControllerResources, listener);

        }

        if (alarmControllerResources.isExternalServerEnabled())
        {
            Log.d(TAG,"External authentication server is enabled");
            return new ExternalServerAuthenticationHandler(alarmControllerResources, listener);
        }
        throw new Exception("No authentication server enabled.");
    }
}

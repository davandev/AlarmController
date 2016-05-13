package com.davan.alarmcontroller.procedures;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.util.Log;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

public class AlarmProcedureFactory
{
    private static final String TAG = AlarmProcedureFactory.class.getSimpleName();

    /**
     * Create an alarmProcedure depending on configuration
     * @param resources
     * @param listener,
     * @return the alarmprocedure
     * @throws Exception when configuration is faulty
     */
    public static AlarmProcedureIf createProcedure(
            AlarmControllerResources resources,
            AlarmProcedureResultListener listener) throws Exception
    {
        Log.d(TAG, "createProcedure");
        if (resources.isFibaroServerEnabled())
        {
            Log.d(TAG,"Creating FibarServerAlarmProcedure");
            return new FibaroServerAlarmProcedure(resources, listener);
        }

        if (resources.isExternalServerEnabled())
        {
            Log.d(TAG,"Creating ExternalServerAlarmProcedure");
            return new ExternalServerAlarmProcedure(resources, listener);
        }
        throw new Exception(resources.getResources().getString(R.string.pref_message_no_server_enabled));
    }
}

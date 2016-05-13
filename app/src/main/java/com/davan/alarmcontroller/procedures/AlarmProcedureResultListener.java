package com.davan.alarmcontroller.procedures;

/**
 * Created by davandev on 2016-04-12.
 **/
public interface AlarmProcedureResultListener
{
    /**
     * Callback from AlarmProcedure when procedure is
     * executed.
     * @param success indicate if procedure was successful
     * @param result result from procedure
     */
    void resultReceived(boolean success, String result);
}

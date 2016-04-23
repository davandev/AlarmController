package com.davan.alarmcontroller.procedures;

/**
 * Created by davandev on 2016-04-12.
 **/
public interface AlarmProcedureResultListener
{
    void resultReceived(boolean success, String result);
}

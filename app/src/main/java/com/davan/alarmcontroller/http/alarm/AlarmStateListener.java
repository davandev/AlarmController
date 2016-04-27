package com.davan.alarmcontroller.http.alarm;

/**
 * Created by davandev on 2016-03-26.
 **/
public interface AlarmStateListener
{
    /**
     * Callback
     * @param alarmState current alarm state
     * @param alarmType current alarm type
     */
    void alarmStateUpdate(String alarmState,String alarmType);
}

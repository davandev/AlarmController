package com.davan.alarmcontroller.http.alarm;

/**
 * Created by davandev on 2016-03-26.
 **/
public interface AlarmStateListener
{
    void alarmStateUpdate(String alarmState,String alarmType);
}

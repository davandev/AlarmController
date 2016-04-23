package com.davan.alarmcontroller.procedures;

/**
 * Created by davandev on 2016-04-12.
 **/
public interface AlarmProcedureIf
{
    void arm(String alarmType);
    void disarm(String alarmType,String pin);

}

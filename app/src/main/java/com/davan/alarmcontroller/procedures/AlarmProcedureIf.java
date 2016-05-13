package com.davan.alarmcontroller.procedures;

/**
 * Created by davandev on 2016-04-12.
 **/
public interface AlarmProcedureIf
{
    /**
     * Arm Fibaro alarm system
     * @param alarmType
     */
    void arm(String alarmType);

    /**
     * Disarm Fibaro alarm system
     * @param alarmType
     * @param pin, user pincode
     */
    void disarm(String alarmType,String pin);

}

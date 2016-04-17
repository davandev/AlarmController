package com.davan.alarmcontroller.authentication;

/**
 * Created by davandev on 2016-04-12.
 */
public interface AuthenticationHandlerIf
{
    void arm(String alarmType);
    void disarm(String alarmType,String pin);

}

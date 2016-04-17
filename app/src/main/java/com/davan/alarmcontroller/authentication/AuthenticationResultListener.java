package com.davan.alarmcontroller.authentication;

/**
 * Created by davandev on 2016-04-12.
 */
public interface AuthenticationResultListener
{
    public void resultReceived(boolean success, String result);
}

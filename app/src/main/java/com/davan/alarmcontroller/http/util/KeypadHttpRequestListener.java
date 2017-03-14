package com.davan.alarmcontroller.http.util;

/**
 * Created by davandev on 2016-03-21.
 */
public interface KeypadHttpRequestListener {
    boolean wakeup();
    boolean tts(String message);
}

package com.davan.alarmcontroller.http.util;

import com.davan.alarmcontroller.http.NanoHTTPD;

/**
 * Created by davandev on 2016-03-21.
 */
public interface KeypadHttpRequestListener {
    boolean wakeup();
    boolean tts(String message);
    boolean play(String file);
    NanoHTTPD.Response getSpeechFile();
    NanoHTTPD.Response getLogFile();
}

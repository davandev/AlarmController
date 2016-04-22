package com.davan.alarmcontroller.http;

import android.util.Log;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

/**
 * Created by davandev on 2016-04-20.
 **/
public class TelegramActivity implements RequestDispatcherResultListener
{
    private static final String TAG = TelegramActivity.class.getSimpleName();

    private RequestDispatcher dispatcher;

    private String token;
    private String chatId;
    private AlarmControllerResources resources;

    public TelegramActivity(AlarmControllerResources res)
    {
        resources = res;
        token = resources.getTelegramToken();
        chatId = resources.getTelegramChatId();
    }

    public void sendMessage(String message)
    {
        Log.d(TAG, "sendMessage [" + message + "]");
        String url = resources.getTelegramSendMessageUrl(token,chatId);
        dispatcher = new RequestDispatcher(this);
        dispatcher.execute(url + message,"","", "true");
    }

    @Override
    public void resultReceived(String result)
    {
        Log.d(TAG,"resultReceived");
    }
}

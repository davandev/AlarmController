package com.davan.alarmcontroller.http;

import android.util.Log;

import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by davandev on 2016-04-20.
 * @TODO: currently it is not possible to send messages that contain å ä ö.
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
        ArrayList<String> chatIds = resources.getAllTelegramChatIds();
        for (String chatId1 : chatIds)
        {
            String url = resources.getTelegramSendMessageUrl(token, chatId1);
            Log.d(TAG, "TelegramUrl:" + url);
            dispatcher = new RequestDispatcher(this);
            dispatcher.execute(url + message, "", "", "true");
        }
    }
    @Override
    public void resultReceived(String result)
    {
        Log.d(TAG,"resultReceived");
    }
}

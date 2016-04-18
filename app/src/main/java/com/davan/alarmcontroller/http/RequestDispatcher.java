package com.davan.alarmcontroller.http;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class RequestDispatcher extends AsyncTask<String, Void, String>
{
    private static final String TAG = RequestDispatcher.class.getName();

    private RequestDispatcherResultListener listener;

    public RequestDispatcher(RequestDispatcherResultListener callbackListener)
    {
        listener = callbackListener;
    }

    @Override
    protected String doInBackground(String... urls)
    {

        // params comes from the execute() call: params[0] is the url.
        try {
            Log.d(TAG, "Url:" + urls[0] +" AuthenticatingUser:" + urls[1]);
            return sendRequest(urls[0],urls[1],urls[2], urls[3]);
        }
        catch (IOException e)
        {
            Log.d(TAG,"Caught exception when connection to page : " + e.getMessage());
            return "No contact with server";
        }
    }
        // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result)
    {
        Log.d(TAG,"onPostExecute Result:" + result);
        listener.resultReceived(result);
    }

    private String sendRequest(String address,String user, String passwd, String readMyInput) throws IOException
    {
        final String username = user;
        final String password = passwd;
        final String waitForInput = readMyInput;

        InputStream is = null;
        int len = 1000;

        try {
            Authenticator.setDefault(new Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            // Starts the query
            conn.connect();
            if( waitForInput.compareTo("false") ==0 )
            {
                Log.d(TAG, "Discard response");
                conn.disconnect();
                return "200";
            }
            int response = conn.getResponseCode();
            Log.d(TAG, "Response code : " + response);

            is = conn.getInputStream();
            String contentAsString = Integer.toString(response);
            contentAsString += readIt(is, len);
            return contentAsString;

        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader buffReader = new BufferedReader(reader);
        StringBuilder result = new StringBuilder();
        String line;
        while((line = buffReader.readLine()) != null)
        {
            result.append(line);
        }
        return result.toString();
    }
}
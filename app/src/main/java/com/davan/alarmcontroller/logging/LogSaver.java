package com.davan.alarmcontroller.logging;

/**
 * Created by davandev on 2016-05-10.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.davan.alarmcontroller.R;

public class LogSaver extends Activity
{
    private static final String TAG = LogSaver.class.getSimpleName();

    private StringBuilder log;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logview);
        try
        {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            log=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                log.append(line + "\n");
            }
            TextView tv = (TextView)findViewById(R.id.textView1);
            tv.setText(log.toString());
        }
        catch (IOException e)
        {
            Log.w(TAG,"Failed to compose log file");
        }


        //convert log to string
        final String logString = new String(log.toString());

        //create text file in SDCard
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/myLogcat");
        dir.mkdirs();
        File file = new File(dir, "logcat.txt");

        try
        {
            //to write logcat in text file
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(logString);
            osw.flush();
            osw.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendLog(View v)
    {
        Log.d(TAG, "SendLog");

        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Report from AlarmController Keypad");
        intent.setData(Uri.parse("mailto:davan_at_work@hotmail.com"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File fileLocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "myLogcat/logcat.txt");
        Uri path = Uri.fromFile(fileLocation);
        intent.putExtra(Intent.EXTRA_STREAM, path);
        try
        {
            startActivity(Intent.createChooser(intent, "Send mail..."));
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(LogSaver.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }
}
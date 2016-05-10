package com.davan.alarmcontroller.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.davan.alarmcontroller.BuildConfig;
import com.davan.alarmcontroller.R;
import com.davan.alarmcontroller.logging.LogSaver;

/**
 * Created by davandev on 2016-04-25.
 **/
public class AboutDialog
{
        private static final String TAG = AboutDialog.class.getSimpleName();

        public static void showDialog(final Activity callingActivity)
        {
            Log.d(TAG, "showDialog");
            AlertDialog.Builder alert = new AlertDialog.Builder(callingActivity);
            LayoutInflater inflater = callingActivity.getLayoutInflater();
            View layout=inflater.inflate(R.layout.about_dialog,null);
            alert.setView(layout);
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            final TextView versionField=(TextView)layout.findViewById(R.id.version);
            versionField.setText("Version: " + versionName + " " + versionCode);

            alert.setNeutralButton("ViewLogs", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    Log.d(TAG, "viewLogs");
                    Intent intent = new Intent(callingActivity, LogSaver.class);
                    callingActivity.startActivity(intent);
                }
            });
            AlertDialog dialog = alert.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
        }

        public void viewLogs(View view)
        {
            Log.d(TAG, "viewLog");

        }
    }

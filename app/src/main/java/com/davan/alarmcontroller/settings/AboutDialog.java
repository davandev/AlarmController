package com.davan.alarmcontroller.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.davan.alarmcontroller.BuildConfig;
import com.davan.alarmcontroller.R;

/**
 * Created by davandev on 2016-04-25.
 **/
public class AboutDialog
{
        private static final String TAG = AboutDialog.class.getSimpleName();

        public static void showDialog(Activity callingActivity)
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

            AlertDialog dialog = alert.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
        }
    }

package com.davan.alarmcontroller.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.davan.alarmcontroller.R;

/**
 * Created by davandev on 2016-04-16.
 */
public class SettingsLauncher
{
    private static final String TAG = SettingsLauncher.class.getSimpleName();

    public static void verifySettingsPassword(Activity callingActivity,  AlarmControllerResources res, View view)
    {
        Log.d(TAG, "verifySettingsPassword");
        final Activity currentActivity = callingActivity;
        final AlarmControllerResources resources = res;
        AlertDialog.Builder alert = new AlertDialog.Builder(callingActivity);
        LayoutInflater inflater = callingActivity.getLayoutInflater();
        View layout=inflater.inflate(R.layout.password_dialog,null);
        final EditText usernameInput=(EditText)layout.findViewById(R.id.passwordfield);
        alert.setView(layout);

        alert.setTitle("Unlock settings menu");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value1 = usernameInput.getText().toString();
                if (value1.equals(resources.getSettingsPassword())) {
                    showSettings(currentActivity);
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
    public static void showSettings(Activity currentActivity)
    {
        Log.d(TAG, "showSettings");
        Intent intent = new Intent(currentActivity, SettingsActivity.class);
        currentActivity.startActivity(intent);
    }
}

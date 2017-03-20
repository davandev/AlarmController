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
    public class UsageDialog
    {
        private static final String TAG = AboutDialog.class.getSimpleName();

        /**
         * Show the about dialog
         * @param callingActivity
         */
        public static void showDialog(final Activity callingActivity,String message)
        {
            Log.d(TAG, "showDialog");
            AlertDialog.Builder alert = new AlertDialog.Builder(callingActivity);
            alert.setTitle("Usage");
            alert.setMessage(message)
                    .setPositiveButton("close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    });
            AlertDialog dialog = alert.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
        }
    }

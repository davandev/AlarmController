package com.davan.alarmcontroller;
/**
 * Created by davandev on 2016-04-12.
 **/
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.davan.alarmcontroller.authentication.AlarmProcedureIf;
import com.davan.alarmcontroller.authentication.AlarmProcedureFactory;
import com.davan.alarmcontroller.authentication.AlarmProcedureResultListener;
import com.davan.alarmcontroller.http.WifiConnectionChecker;
import com.davan.alarmcontroller.settings.AlarmControllerResources;

public class Disarm extends AppCompatActivity implements AlarmProcedureResultListener
{
    private static final String TAG = Disarm.class.getSimpleName();

    private String alarmType = "";
    private boolean authenticationOk = false;
    private WifiConnectionChecker wifiChecker;
    private AlarmProcedureIf handler;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private String authenticatedUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags ^= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        setContentView(R.layout.activity_keypad);
        Intent myIntent = getIntent(); // gets the previously created intent
        alarmType = myIntent.getStringExtra(getResources().getString(R.string.alarm_type));

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        wifiChecker = new WifiConnectionChecker(connMgr);

        AlarmControllerResources resources = new AlarmControllerResources(
                PreferenceManager.getDefaultSharedPreferences(this),
                getSharedPreferences("com.davan.alarmcontroller.users", 0),
                getResources());
        try
        {
            handler = AlarmProcedureFactory.createProcedure(resources, this);
        }
        catch(Exception e)
        {
            Toast.makeText(getBaseContext(), R.string.pref_message_no_server_enabled, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        if (!authenticationOk)
        {
            // Return back to locked screen on pause when not unlocked.
            Intent intent = new Intent(this, Armed.class);
            intent.putExtra(getResources().getString(R.string.alarm_type), alarmType);
            startActivity(intent);
        }

    }

    public void buttonPushed(View view)
    {
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        if(buttonText.compareTo("OK") == 0)
        {
            String password = ((EditText) findViewById(R.id.editText2)).getText().toString();

            Log.d(TAG, "OK pushed");
            if (wifiChecker.isConnectionOk() )
            {
                authenticationOk = false;
                handler.disarm(alarmType, password);
            }
            else
            {
                Toast.makeText(getBaseContext(), R.string.pref_message_no_network_connection, Toast.LENGTH_LONG).show();
            }
        }

        else if (buttonText.compareTo("<<") ==0)
        {
            int length = ((EditText) findViewById(R.id.editText2)).getText().length();
            if (length > 0)
            {
                ((EditText) findViewById(R.id.editText2)).getText().delete(length - 1, length);
            }
        }
        else
        {
            ((EditText) findViewById(R.id.editText2)).append(buttonText);
        }
    }
    @Override
    public void resultReceived(boolean success, String result)
    {
        Log.d(TAG, "resultReceived");
        authenticationOk = success;
        authenticatedUser = result;
        // create Intent to take a picture and return control to the calling application
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "AlarmController");
        imagesFolder.mkdirs(); // <----
        File image = new File(imagesFolder, "image_001.jpg");
        Uri uriSavedImage = Uri.fromFile(image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
*/
        if (authenticationOk)
        {
            Toast.makeText(getBaseContext(), "Välkommen hem "+authenticatedUser, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, Disarmed.class);
            startActivity(intent);
        }
        else
        {
            ((EditText) findViewById(R.id.editText2)).setText("");
            Toast.makeText(getBaseContext(), "Fel lösenord", Toast.LENGTH_LONG).show();
        }

    }
}

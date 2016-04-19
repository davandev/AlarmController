package com.davan.alarmcontroller.camera;

/**
 * Created by davandev on 2016-04-18.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.davan.alarmcontroller.MainActivity;
import com.davan.alarmcontroller.R;

public class CameraActivity extends Activity implements SurfaceHolder.Callback
{
    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    boolean mPreviewRunning = false;
    SurfaceHolder mSurfaceHolder;
    SurfaceView mSurfaceView;
    private Camera camera; // camera object
    File mediaFile;
    private static final String IMAGE_DIRECTORY_NAME = "Captured_Images";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView); //new SurfaceView(this);

        Log.d(TAG, "oncreate");
        requestPermission(this);
        //takePicture();
    }

    public void takePicture()
    {

       // mSurfaceView = new SurfaceView(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void closeCamera()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void sendPicture()
    {
    }

    public void requestPermission(Activity currentActivity)
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(currentActivity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.CAMERA)) {
                Log.d(TAG,"Needs permission show rationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                Log.d(TAG, "Needs permission no rationale");

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            takePicture();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG,"Permission granted");
                    takePicture();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d(TAG,"Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    Camera.PictureCallback jpegCallBack=new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            Log.d(TAG,"onPictureTaken");
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),IMAGE_DIRECTORY_NAME);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists())
            {
                if (!mediaStorageDir.mkdirs())
                {
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
            mediaFile = new File(mediaStorageDir.getPath() + File.separator+ "IMG_" + timeStamp + ".png");
            Log.d(TAG, "Picture path : " + mediaFile.getAbsolutePath());
            try
            {
                Bitmap userImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                // set file out stream
                FileOutputStream out = new FileOutputStream(mediaFile);
                // set compress format quality and stream
                userImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            closeCamera();
            sendPicture();

            //camera.stopPreview();
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        if (Camera.getNumberOfCameras() >1)
        {
            camera = Camera.open(1);
        }
/*        Camera camera = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Log.d(TAG,"Found camera: " +camIdx );
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    Log.d(TAG,"Found front camera");
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    return;
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
*/
        //return cam;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        Log.d(TAG,"surfaceChanged");
        if (mPreviewRunning)
        {
            camera.stopPreview();
        }
        Camera.Parameters p = camera.getParameters();
        //p.setPreviewSize(1, 1);

        camera.setParameters(p);
        try {
        	camera.setPreviewDisplay(holder);
            }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        camera.startPreview();
        camera.takePicture(null, null, null, jpegCallBack);
        mPreviewRunning = true;


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.d(TAG,"surfaceDestroyed");
        camera.stopPreview();
        ///mPreviewRunning = false;
        camera.release();

    }
}

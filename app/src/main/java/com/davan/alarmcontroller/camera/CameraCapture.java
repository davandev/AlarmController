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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.davan.alarmcontroller.Disarm;

public class CameraCapture implements SurfaceHolder.Callback
{
    private static final String TAG = CameraCapture.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    boolean mPreviewRunning = false;
    SurfaceHolder mSurfaceHolder;
    SurfaceView mSurfaceView;
    private Camera camera; // camera object
    File mediaFile;
    private static final String IMAGE_DIRECTORY_NAME = "Captured_Images";
    private CameraCaptureResultListener resultListener;

    public CameraCapture(Activity activity, SurfaceView surfaceView, CameraCaptureResultListener listener)
    {
        Log.d(TAG,"Constructor");
        mSurfaceView = surfaceView;
        resultListener = listener;


        Log.d(TAG, "oncreate");
        //requestPermission(activity);
        takePicture();
    }

    public void takePicture()
    {
        Log.d(TAG,"takePicture");
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void closeCamera()
    {
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    public void sendPicture()
    {
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
            resultListener.photoTaken(mediaFile);

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

package com.davan.alarmcontroller.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by davandev on 2016-04-18.
 *
 * SurfaceView view = new SurfaceView(this);
 c.setPreviewDisplay(view.getHolder());
 c.startPreview();
 c.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
 **/
public class CameraCapture
{
    private static final String TAG = CameraCapture.class.getSimpleName();
    public Camera camera;

    public void startCameraCapture(Context contx)
    {
        SurfaceView sv = new SurfaceView(contx);
        camera = Camera.open();
        SurfaceTexture surfaceTexture = new SurfaceTexture(10);

        try
        {
            camera.setPreviewTexture(surfaceTexture);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        camera.setPreviewCallback(new Camera.PreviewCallback()
        {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera)
            {
                Log.v("TAG", "on preview frame called");
            }
            //Thread.sleep(1000);
           // camera.startPreview();
           // camera.takePicture();
        });
    }
}
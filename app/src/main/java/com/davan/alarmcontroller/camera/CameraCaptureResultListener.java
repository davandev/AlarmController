package com.davan.alarmcontroller.camera;

import java.io.File;

/**
 * Created by davandev on 2016-04-19.
 */
public interface CameraCaptureResultListener
{
    void photoTaken(File mediaFile);
}

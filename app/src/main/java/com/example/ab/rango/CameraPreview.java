package com.example.ab.rango;

/**
 * Created by ab on 1/1/15.
 */

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by ab on 1/2/15.
 */
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;
    private int mSurfaceHeight;
    private int mSurfaceWidth;

    public static final String TAG = "CameraPreview";

    public CameraPreview(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    public CameraPreview(Context context) {
        super(context);
    }


    public void connectCamera(Camera camera, int cameraId) {
        mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);

        Log.e(TAG,"width "+mSurfaceWidth+" height: " + mSurfaceHeight);
        setCameraParameters(mSurfaceWidth,mSurfaceHeight);

        // start preview
        startPreview();
    }

    public void releaseCamera() {
        if(mCamera != null) {
            // stop preview
            stopPreview();

            mCamera = null;
        }
    }

    void startPreview() {
        if(mCamera != null && mHolder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "Error setting preview display: " + e.getMessage());
            }
        }
    }

    void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping preview: " + e.getMessage());
            }
        }
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mContext = context;

        // Install a SurfaceHolder.Callback so we get notified
        // when the underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions
        // prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in
        // your activity.
        stopPreview();
    }

    // In order to change preview orientation as the user
    // re-orients the phone, within the surfaceChanged() method
    // of your preview class, first stop the preview with Camera
    // .stopPreview() change the orientation and then start the
    // preview again with Camera.startPreview().
    //
    // Parameters
    // holder 	The SurfaceHolder whose surface has changed.
    // format 	The new PixelFormat of the surface.
    // width 	The new width of the surface.
    // height 	The new height of the surface.
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int w, int h) {
//        // If your preview can change or rotate, take care of
//        // those events here. Make sure to stop the preview
//        // before resizing or reformatting it.
//        if (mHolder.getSurface() == null){
//            // preview surface does not exist
//            return;
//        }
//
//        // stop preview before making changes
//        try {
//            mCamera.stopPreview();
//        } catch (Exception e){
//            // ignore: tried to stop a non-existent preview
//        }

        stopPreview();

        mSurfaceHeight = h;
        mSurfaceWidth = w;

        setCameraParameters(w,h);



        startPreview();
//        // start preview with new settings
//        try {
//            mCamera.setPreviewDisplay(mHolder);
//            mCamera.startPreview();
//
//        } catch (Exception e){
//            Log.d(TAG, "Error starting camera preview: "
//                    + e.getMessage());
//        }
    }

    private void setCameraParameters(int w, int h) {
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // When setting preview size, you must use values from
        // getSupportedPreviewSizes().
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size s = null;
        Log.d(TAG, "surfaceChanged- width: "+w+" hright: "+h);
        s = getOptimalPreviewSize(
                parameters.getSupportedPreviewSizes(), w, h);
        parameters.setPreviewSize(s.width, s.height);
        mCamera.setParameters(parameters);


        mCamera.setDisplayOrientation(90);
    }

    /** A simple algorithm to get the largest size available.
     *  For a more robust version, see CameraPreview.java in
     *  the ApiDemos sample app from Android. */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            Log.d(TAG,"sizes: "+ size.width+"  "+size.height);
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d(TAG,"optimalSize "+ optimalSize.width +"   "+optimalSize.height);
        return optimalSize;
    }
}
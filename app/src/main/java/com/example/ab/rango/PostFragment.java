package com.example.ab.rango;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ab.rango.adapter.MyPagerAdapter;
import com.example.ab.rango.data.RangoContract;
import com.example.ab.rango.util.Utils;
import com.example.ab.rango.widget.IndicatorView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ab on 12/28/14.
 */
public class PostFragment extends Fragment {

    public PostFragment() {
    }

    public static final String LOG_TAG = "PostFragment";

    private Camera mCamera;
    private boolean mHasCamera;
    private CameraPreview mCameraPreview;
    private ImageButton mCameraExit;
    private ImageButton mCaptureButton;
    private ImageButton mButtonPost;

    private EditText itemNameInput;
    private EditText priceInput;
    private EditText descriptionInput;
    private EditText catagoryInput;


    private ArrayList<String> postPictures = new ArrayList<>();

    final int CAMERA_ID_NOT_SET = -1;
    // int _selectedCameraId  = CAMERA_ID_NOT_SET;
    int _selectedCameraId  = 0;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    Camera.Parameters _cameraParameters = null;
    List<Camera.Size> _supportedPictureSizes = null;
    Camera.Size _selectedPictureSize = null;


    /** A safe way to get an instance of the Camera object. */
    private static Camera getCameraInstance(){
        Camera c = null;
        try {
            // attempt to get a Camera instance
            // access the first, back-facing camera on a device with
            // more than one camera.
            // Camera.open(int) to access specific camera
            c = Camera.open();

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e(LOG_TAG,"Camera is not available (in use or does not exist");
        }
        return c; // returns null if camera is unavailable
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);




        final LayoutInflater inf = inflater;
        final ViewGroup vg = container;

        mHasCamera = checkCameraHardware(getActivity());
        showToast("has camera: " + mHasCamera);

        if(mHasCamera)
            // Create an instance of Camera
            // getCameraInstance() defined above in 1.b
            mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of
        // our activity.
        mCameraPreview = new CameraPreview(getActivity());
        final FrameLayout preview =
                (FrameLayout) v.findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);


        mCaptureButton = (ImageButton) v.findViewById(R.id.button_capture);
        mCameraExit = (ImageButton) v.findViewById(R.id.camera_exit);
        mButtonPost = (ImageButton) v.findViewById(R.id.buttonPost);

        // Add a listener to the Capture button
        mCaptureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        takePicture();
                        mCameraExit.setVisibility(View.VISIBLE);
                    }
                });




        mButtonPost.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String title = itemNameInput.getText().toString();
                        String price = priceInput.getText().toString();
                        String description = descriptionInput.getText().toString();
                        String catagory = catagoryInput.getText().toString();

                        Log.d(LOG_TAG, "title: " + title + " + " + "price: " + price);

                        String postPicturesStr = Utils.convertArrayToString(postPictures);

                        ContentValues testValues = new ContentValues();
                        testValues.put(RangoContract.ItemsEntry.COLUMN_ITEM_DESCRIPTION, description);
                        testValues.put(RangoContract.ItemsEntry.COLUMN_ITEM_NAME, title);
                        testValues.put(RangoContract.ItemsEntry.COLUMN_ITEM_PRICE, price);
                        testValues.put(RangoContract.ItemsEntry.COLUMN_POSTER_NAME, "ab");
                        testValues.put(RangoContract.ItemsEntry.COLUMN_POST_DATE, "1/5/15");
                        testValues.put(RangoContract.ItemsEntry.COLUMN_ITEM_PICTURES, postPicturesStr);

                        // ItemsEntry.CONTENT_URI:  content://com.example.ab.rango/items
                        Uri locationUri = getActivity().getContentResolver().insert(
                                RangoContract.ItemsEntry.CONTENT_URI, testValues);

                        preview.removeAllViewsInLayout();
                        preview.addView(mCameraPreview);
                        preview.addView(mCameraExit);
                        postPictures = new ArrayList<>();
                        ((MainActivity) getActivity()).mViewPager.setCurrentItem(0);

                        mCameraExit.setVisibility(View.INVISIBLE);
                        mButtonPost.setVisibility(View.INVISIBLE);

                    }
                });

        mCameraExit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mButtonPost.setVisibility(View.VISIBLE);

                        String conv2str = Utils.convertArrayToString(postPictures);
                        Log.d(LOG_TAG,"postPictures: "+postPictures);
                        Log.d(LOG_TAG,"array to string: "+conv2str);
                        Log.d(LOG_TAG,"string to array: "+ Utils.convertStringToArray(conv2str));

                        ViewGroup parent = (ViewGroup) mCameraExit.getParent();
                        parent.removeView(mCameraExit);

                        preview.removeAllViewsInLayout();
                        //View C = inf.inflate(R.layout.item_newsfeed, vg, false);
                        View C = inf.inflate(R.layout.item_discription, vg, false);
                        preview.addView(C);

                        itemNameInput = (EditText) C.findViewById(R.id.editName);
                        priceInput = (EditText) C.findViewById(R.id.editPrice);
                        descriptionInput = (EditText) C.findViewById(R.id.editDescription);
                        catagoryInput = (EditText) C.findViewById(R.id.editCatagory);


                        ViewPager viewPager = (ViewPager) C.findViewById(R.id.viewpager);
                        IndicatorView indicatorView = (IndicatorView) C.findViewById(R.id.indicator);

                        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
                        params.topMargin = (int) pixels;  // 15dp
                        preview.setBackgroundColor(Color.parseColor("#646464"));
                        viewPager.setLayoutParams(params);

//                        int size = position + 1;
//                        if (size > 10)
//                            size = 10;

                        indicatorView.setUpView(postPictures.size());
                        indicatorView.setSelectIndex(0);

                        // use a simplepageradapter or sumin` instead of
                        MyPagerAdapter pageAdaper = new MyPagerAdapter(getActivity(),
                                postPictures.size(), postPictures);
                        viewPager.setAdapter(pageAdaper);

                        viewPager.setCurrentItem(0);
                        viewPager.setOnPageChangeListener(
                                new MyPagerAdapter.MyPagerChangeListener(indicatorView));



                    }
                });

        return v;
    }

    void takePicture() {
        // set camera parameters here... picture size, gps, orientation...
        if(_cameraParameters != null) {

            // _cameraParameters.setPictureSize(960,720);
//            _cameraParameters.setPictureSize(_selectedPictureSize.width,
//                    _selectedPictureSize.height);
//
//            mCamera.setParameters(_cameraParameters);
        }

        //public final void takePicture (
        //   Camera.ShutterCallback shutter,
        //   Camera.PictureCallback raw,
        //   Camera.PictureCallback jpeg)
        mCamera.takePicture(null, null, mPicture);
    }

    void openSelectedCamera() {
        String message = null;

        releaseSelectedCamera();
        if(_selectedCameraId != CAMERA_ID_NOT_SET) {
            try {
                mCamera = Camera.open(_selectedCameraId);
                message = String.format("Opened Camera ID: %d", _selectedCameraId);

                CameraPreview cameraPreview = mCameraPreview;
                cameraPreview.connectCamera(mCamera, _selectedCameraId);

                _cameraParameters = mCamera.getParameters();
                _supportedPictureSizes = _cameraParameters.getSupportedPictureSizes();

                Log.d(LOG_TAG,"camera parameters: "+_cameraParameters);
                for(Camera.Size pictureSize:_supportedPictureSizes) {
                    Log.d(LOG_TAG, "supported picture sizes: " +
                            String.format("%d x %d", pictureSize.width, pictureSize.height));
                    if(pictureSize.width < 1000 && pictureSize.height < 1000) {
                        _selectedPictureSize = pictureSize;
                        break;
                    }

                }

                if(_selectedPictureSize == null)
                    _selectedPictureSize = _cameraParameters.getPictureSize();

                mCamera.stopPreview();
                _cameraParameters.setPictureSize(_selectedPictureSize.width,
                        _selectedPictureSize.height);
                mCamera.setParameters(_cameraParameters);
                mCamera.startPreview();

            } catch (Exception ex) {
                message = "Unable to open camera: " + ex.getMessage();
                Log.e(LOG_TAG, message);

            }
        }

        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    void releaseSelectedCamera() {
        if(mCamera != null) {
            mCameraPreview.releaseCamera();

            mCamera.release();
            mCamera = null;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        // mPreview.getHolder().removeCallback(mPreview);
        // release the camera immediately on pause event
        releaseSelectedCamera();
    }


    /**
     * On fragment getting resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "postFragment on resume");
        openSelectedCamera();
    }



    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

            int i = bytes.length;
            Log.d(LOG_TAG, String.format("picture bytes = %d", i));

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(LOG_TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(pictureFile));
                bos.write(bytes);
                bos.close();
                Log.d(LOG_TAG, "Picture AbsolutePath: " + pictureFile.getAbsolutePath());
                Log.d(LOG_TAG, "Picture saved as: " + pictureFile.getName());
                postPictures.add(pictureFile.getAbsolutePath());


            } catch (FileNotFoundException e) {
                Log.d(LOG_TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(LOG_TAG, "Error accessing file: " + e.getMessage());
            }

            // screen preview stops when a picture is taken restart it
            // when the handler is done
            mCamera.startPreview();
        }
    };

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Rango");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Rango", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }



    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}


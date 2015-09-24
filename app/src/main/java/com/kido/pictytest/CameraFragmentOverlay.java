package com.kido.pictytest;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.widget.Toolbar;

import com.software.shell.fab.ActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class CameraFragmentOverlay extends Fragment implements SurfaceHolder.Callback {
    Runnable runUpdate;
    Runnable runStartAnimation;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private View rootView;
    private Context fContext;
    private MainActivity mActivity;
    private Toolbar mToolbar;
    private Camera camera;
    private int camID = 0;
    private ActionButton btnBack;
    private ActionButton btnFlash;
    private ActionButton btnRotare;
    private Bitmap bm;
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };
    private Camera.PictureCallback raw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
    private Camera.PictureCallback jpg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data != null) {
                try {
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    bm = BitmapFactory.decodeByteArray(data, 0,
                            (data != null) ? data.length : 0);
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(camID, info);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                         Notice that width and height are reversed
                        Bitmap scaled = Bitmap.createScaledBitmap(bm,
                                screenHeight, screenWidth, true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
//                         Setting post rotate to 90
                        Matrix mtx = new Matrix();
                        mtx.postRotate(90);
                        if (camID == Camera.CameraInfo.CAMERA_FACING_FRONT)
                            mtx.postRotate(180);
                        // Rotating Bitmap
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                                bm.getHeight(), mtx, true);
                    } else {// LANDSCAPE MODE
                        Bitmap scaled = Bitmap.createScaledBitmap(bm,
                                screenWidth, screenHeight, true);
                        bm = scaled;
                    }
                } catch (Exception e) {
                } catch (Error e) {
                }
            }
            try {
                File saveDir = new File("/sdcard/PictyExample/");

                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }


                mActivity.getActionButton().playHideAnimation();

                String fname = String.format("/sdcard/PictyExample/%d.jpg", System.currentTimeMillis());
                FileOutputStream os = new FileOutputStream(fname);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();

                try {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                    Fragment fragment = PhotoFragment.newInstance(fname);
                    fragmentTransaction.replace(R.id.containerMain, fragment, fragment.getClass().getSimpleName());
                    fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    Log.e("Ad edit", e.toString());
                }
            } catch (Exception e) {
            }
            releaseCameraAndPreview();

        }
    };

    {
        runStartAnimation = new Runnable() {
            public void run() {
//                cameraConnect();
                mActivity.setIsFAB(true);
                mActivity.setBigFAB(true);
                mActivity.updateFAB();
                btnBack.setVisibility(View.VISIBLE);
                btnFlash.setVisibility(View.VISIBLE);
                btnRotare.setVisibility(View.VISIBLE);
                mActivity.getActionButton().playShowAnimation();
                btnBack.playShowAnimation();
                btnRotare.playShowAnimation();
                btnFlash.playShowAnimation();
            }
        };
    }

    {
        runUpdate = new Runnable() {
            public void run() {
                if (camera == null) {
                    cameraConnect();
                }
            }
        };
    }

    public CameraFragmentOverlay() {
        // Required empty public constructor
    }

    public void takePhoto() {
        try {
            if (camera != null) {
                camera.takePicture(shutterCallback, raw, jpg);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_camera_overlay, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fContext = getActivity().getApplicationContext();
        mActivity = (MainActivity) getActivity();
        mActivity.getmToolbar().setVisibility(View.GONE);

        btnBack = (ActionButton) rootView.findViewById(R.id.aBtnBack);
        btnBack.setImageResource(R.drawable.ic_arrow_back);
        btnBack.setButtonColor(Color.TRANSPARENT);
        btnBack.setStrokeColor(Color.BLACK);
        btnBack.setStrokeWidth(1.5f);
        btnBack.setButtonColorPressed(Color.RED);
        btnBack.removeShadow();
        btnBack.setShowAnimation(ActionButton.Animations.FADE_IN);
        btnBack.setHideAnimation(ActionButton.Animations.FADE_OUT);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFlash.playHideAnimation();
                btnBack.playHideAnimation();
                btnRotare.playHideAnimation();
                mActivity.getActionButton().playHideAnimation();
                mActivity.onBackPressed();

            }
        });
        btnFlash = (ActionButton) rootView.findViewById(R.id.aBtnFlash);
        btnFlash.setImageResource(R.drawable.ic_flash_on);
        btnFlash.setButtonColor(Color.TRANSPARENT);
        btnFlash.setStrokeColor(Color.BLACK);
        btnFlash.setStrokeWidth(1.5f);
        btnFlash.setButtonColorPressed(Color.RED);
        btnFlash.removeShadow();
        btnFlash.setShowAnimation(ActionButton.Animations.FADE_IN);
        btnFlash.setHideAnimation(ActionButton.Animations.FADE_OUT);
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (camID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        return;
                    }

                    Camera.Parameters params = camera.getParameters();
                    if (params.getFlashMode().compareTo(Camera.Parameters.FLASH_MODE_OFF) == 0) {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        btnFlash.setImageResource(R.drawable.ic_flash_on);
                    } else {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        btnFlash.setImageResource(R.drawable.ic_flash_off);
                    }
                    camera.setParameters(params);
                } catch (Exception e) {
                }
            }
        });
        btnRotare = (ActionButton) rootView.findViewById(R.id.aBtnRotare);
        btnRotare.setImageResource(R.drawable.ic_loop);
        btnRotare.setButtonColor(Color.TRANSPARENT);
        btnRotare.setStrokeColor(Color.BLACK);
        btnRotare.setStrokeWidth(1.5f);
        btnRotare.setButtonColorPressed(Color.RED);
        btnRotare.removeShadow();
        btnRotare.setShowAnimation(ActionButton.Animations.FADE_IN);
        btnRotare.setHideAnimation(ActionButton.Animations.FADE_OUT);
        btnRotare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCameraAndPreview();
                if (camID == Camera.CameraInfo.CAMERA_FACING_BACK)
                    camID = Camera.CameraInfo.CAMERA_FACING_FRONT;
                else
                    camID = Camera.CameraInfo.CAMERA_FACING_BACK;

                try {
                    cameraConnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (savedInstanceState == null) {
            mActivity.getmToolbar().setVisibility(View.GONE);
//            startAnimation();

        }
        surfaceView = (SurfaceView) rootView.findViewById(R.id.id_stream);
//        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
//        surfaceView.setLayoutParams(lp);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        surfaceHolder.setSizeFromLayout();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(700);
//                    mActivity.runOnUiThread(runUpdate);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        t.start();
//        cameraConnect();
    }

    public void cameraConnect() {
        if (checkCameraHardware(fContext)) {
            try {
                releaseCameraAndPreview();
                WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                ViewGroup.LayoutParams lp = rootView.getLayoutParams();
                lp.width = size.x; // required width
                lp.height = size.y; // required height
                rootView.setLayoutParams(lp);
                android.view.ViewGroup.LayoutParams lv = surfaceView.getLayoutParams();
                lv.width = size.x; // required width
                lv.height = size.y; // required height
                surfaceView.setLayoutParams(lv);
                surfaceHolder.setSizeFromLayout();

                camera = Camera.open(camID);
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                setCameraDisplayOrientation();
            } catch (Exception e) {
                Toast.makeText(fContext, e.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(fContext, "Camera is not detected on the device", Toast.LENGTH_SHORT).show();
        }

    }

    public void setCameraDisplayOrientation() {
        if (camera == null) {
            Log.d("Warning", "setCameraDisplayOrientation - camera null");
            return;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(camID, info);

        WindowManager winManager = (WindowManager) fContext.getSystemService(Context.WINDOW_SERVICE);
        int rotation = winManager.getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera == null) {
            Log.d("Warning", "setCameraDisplayOrientation - camera null");
            return;
        }
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if (surfaceHolder == null)//check if the surface is ready to receive camera data
            return;

        try {
            camera.stopPreview();
        } catch (Exception e) {
            //this will happen when you are trying the camera if it's not running
        }

        //now, recreate the camera preview
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            setCameraDisplayOrientation();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCameraAndPreview();
    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public void startAnimation() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(400);
                    mActivity.runOnUiThread(runStartAnimation);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    public void endAnimation() {
        Animation animTop = AnimationUtils.loadAnimation(fContext, R.anim.top_from_camera);
        Animation animation = AnimationUtils.loadAnimation(fContext, R.anim.bottom_from_camera);
    }


    @Override
    public void onResume() {
        super.onResume();
        mActivity.setIsFAB(false);
        mActivity.setBigFAB(true);
        mActivity.updateFAB();
        btnBack.setVisibility(View.INVISIBLE);
        btnRotare.setVisibility(View.INVISIBLE);
        btnFlash.setVisibility(View.INVISIBLE);
        if (camera == null) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        TimeUnit.MILLISECONDS.sleep(400);
                        mActivity.runOnUiThread(runUpdate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        startAnimation();

    }


    @Override
    public void onPause() {
        super.onPause();
        btnBack.playHideAnimation();
        btnRotare.playHideAnimation();
        btnFlash.playHideAnimation();
        releaseCameraAndPreview();

    }

    public ActionButton getBtnBack() {
        return btnBack;
    }

    public void setBtnBack(ActionButton btnBack) {
        this.btnBack = btnBack;
    }

    public ActionButton getBtnFlash() {
        return btnFlash;
    }

    public void setBtnFlash(ActionButton btnFlash) {
        this.btnFlash = btnFlash;
    }

    public ActionButton getBtnRotare() {
        return btnRotare;
    }

    public void setBtnRotare(ActionButton btnRotare) {
        this.btnRotare = btnRotare;
    }
}

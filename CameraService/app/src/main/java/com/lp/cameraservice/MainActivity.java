package com.lp.cameraservice;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import java.io.IOException;


public class MainActivity extends Activity implements SurfaceHolder.Callback{

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;
    private LayoutInflater controlInflater = null;
    private ImageView viewPhoto;
    private int windowWidth;
    private int windowHeight;
    private LayoutParams layoutParams;
    private View viewControl;

    public void onCreate(Bundle savedInstanceState) {

         super.onCreate(savedInstanceState);
         setContentView(R.layout.main);
         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

         windowWidth = getWindowManager().getDefaultDisplay().getWidth();
         windowHeight = getWindowManager().getDefaultDisplay().getHeight();


         getWindow().setFormat(PixelFormat.UNKNOWN);
         surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
         surfaceHolder = surfaceView.getHolder();
         surfaceHolder.addCallback(this);
         surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

         controlInflater = LayoutInflater.from(getBaseContext());
         viewControl = controlInflater.inflate(R.layout.control, null);
         LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
         this.addContentView(viewControl, layoutParamsControl);



         viewPhoto = (ImageView) viewControl.findViewById(R.id.image_view);



        viewPhoto.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layoutParams = (LayoutParams) viewPhoto.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();

                        if (x_cord > windowWidth) {
                            x_cord = windowWidth;
                        }
                        if (y_cord > windowHeight) {
                            y_cord = windowHeight;
                        }

                        layoutParams.leftMargin = x_cord - 20;
                        layoutParams.topMargin = y_cord - 65;

                        viewPhoto.setLayoutParams(layoutParams);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback(){
        public void onShutter() {


        }};

    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback(){


        public void onPictureTaken(byte[] arg0, Camera arg1) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {

                e.printStackTrace();
            }

        }};

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback(){

        public void onPictureTaken(byte[] arg0, Camera arg1) {

            Bitmap bitmapPicture= BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
            viewPhoto.setImageBitmap(bitmapPicture);

        }};


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if(previewing){
          camera.stopPreview();
          previewing = false;
        }

        if (camera != null){
             try {
              camera.setPreviewDisplay(surfaceHolder);
              camera.startPreview();
              previewing = true;
             } catch (IOException e) {

              e.printStackTrace();
             }
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {

      camera = Camera.open();
    }


    public void surfaceDestroyed(SurfaceHolder holder) {

        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    public void screenShot(){
        Bitmap screenShotBitmap;
        View screenShotView = viewControl.getRootView();
        screenShotView.setDrawingCacheEnabled(true);
        screenShotBitmap = Bitmap.createBitmap(screenShotView.getDrawingCache());
        screenShotView.setDrawingCacheEnabled(false);
        viewPhoto.setImageBitmap(screenShotBitmap);

    }

}
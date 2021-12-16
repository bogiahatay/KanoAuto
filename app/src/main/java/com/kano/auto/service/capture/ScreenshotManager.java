package com.kano.auto.service.capture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.kano.auto.AutoApp;
import com.kano.auto.base.SyncTask;
import com.kano.auto.utils.MLog;

import java.nio.ByteBuffer;

@SuppressLint("StaticFieldLeak")
public class ScreenshotManager {
    private String TAG = "ScreenshotManager";

    private static final String CAPTURE_NAME = "screen_capture";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    public static ScreenshotManager instance = null;
    public Context context = null;
    private Boolean running = false;
    private Intent mIntent;
    //    private int heightPixels;
//    private int widthPixels;
    private int width;
    private int height;

    public static ScreenshotManager getInstance() {
        if (instance == null) {
            instance = new ScreenshotManager();
        }
        return instance;
    }


    private ScreenshotManager() {
        context = AutoApp.instance;
        initDisplayMetrics();
    }

    public void initDisplayMetrics() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int density = displayMetrics.densityDpi;
        WindowManager ws = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = ws.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        width = size.x;
        height = size.y;
    }

    public void requestScreenshotPermission(@NonNull Activity activity, int requestId) {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), requestId);
    }


    public void onActivityResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            mIntent = data;
            createVirtualDisplay();
        } else {
            mIntent = null;
        }
    }

    @SuppressLint("WrongConstant")
    private void createVirtualDisplay() {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        width = size.x;
        height = size.y;

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, mIntent);
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        imageReader.setOnImageAvailableListener(reader -> {
            running = true;
        }, null);

        virtualDisplay = mediaProjection.createVirtualDisplay(CAPTURE_NAME, width, height, density,
                VIRTUAL_DISPLAY_FLAGS, imageReader.getSurface(), new VirtualDisplay.Callback() {

                    public void onPaused() {
//                        MLog.e("onPaused");
                    }

                    public void onResumed() {
//                        MLog.e("onResumed");
                    }

                    public void onStopped() {
//                        MLog.e("onStopped");
                    }
                }, null);

    }

    long time = 0;
    MediaProjection mediaProjection;
    VirtualDisplay virtualDisplay;
    ImageReader imageReader;

    @SuppressLint("WrongConstant")
    public void takeScreenshot(IOnScreenshot onScreenshot) {
        time = System.currentTimeMillis();
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            public Bitmap doInBackground(Void... voids) {
                if (mediaProjection == null) {
                    MLog.e("MediaProjection null");
                    return null;
                }
                if (!running) {
                    MLog.e("ImageReader not available");
                    return null;
                }

                Image image = null;
                Bitmap bmTemp = null;
                Bitmap bitmap = null;
                ByteBuffer buffer = null;

                 try {
                    image = imageReader.acquireLatestImage();
                    if (image != null) {
//                        MLog.e("--------------");
                        int width = image.getWidth();
                        int height = image.getHeight();
//                        MLog.e("ImageReader", width + "x" + height);
//                        MLog.e("DisplayRealSize", ScreenshotManager.this.width + "x" + ScreenshotManager.this.height);
                        Image.Plane[] planes = image.getPlanes();
                        buffer = planes[0].getBuffer();

                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * width;

                        // create bitmap
                        bmTemp = Bitmap.createBitmap(width + (int) ((float) rowPadding / (float) pixelStride), height, Bitmap.Config.ARGB_8888);
                        bmTemp.copyPixelsFromBuffer(buffer);
//                        MLog.e("BitmapTemp", bmTemp.getWidth() + "x" + bmTemp.getHeight());

                        //
                        bitmap = Bitmap.createBitmap(bmTemp, 0, 0, ScreenshotManager.this.width, ScreenshotManager.this.height);
//                        MLog.e("Bitmap", bitmap.getWidth() + "x" + bitmap.getHeight());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (image != null) {
                        image.close();
                    }
                    if (buffer != null) {
                        buffer.clear();
                    }
                    if (bmTemp != null) {
                        bmTemp.recycle();
                    }
                }
                return bitmap;
            }

            @Override
            public void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                long diff = System.currentTimeMillis() - time;
//                MLog.i(TAG, diff + " ms " + (bitmap == null ? "null" : (widthPixels + "x" + heightPixels)));
                onScreenshot.onComplete(bitmap, diff);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stop() {
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        if (imageReader != null) {
            imageReader.close();
        }
    }

    public void release() {
        stop();
    }
}
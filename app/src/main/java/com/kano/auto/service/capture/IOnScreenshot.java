package com.kano.auto.service.capture;

import android.graphics.Bitmap;

public interface IOnScreenshot {
    void onComplete(Bitmap bitmap, long timeCreate);
}
package com.tngtech.jgiven.android.example;


import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class ScreenshotUtil {

    public static byte[] takeScreenshot(Activity activity) {
        View view = activity.getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}

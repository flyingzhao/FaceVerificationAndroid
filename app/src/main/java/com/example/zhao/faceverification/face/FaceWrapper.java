package com.example.zhao.faceverification.face;

import android.graphics.Bitmap;

import org.opencv.core.Rect;

/**
 * Created by zhao on 2016/12/12.
 */
public class FaceWrapper {
    public Bitmap faceBitmap;
    public Rect faceRect;

    public FaceWrapper(Bitmap faceBitmap, Rect faceRect) {
        this.faceBitmap = faceBitmap;
        this.faceRect = faceRect;
    }

    public Bitmap getFaceBitmap() {
        return faceBitmap;
    }

    public Rect getFaceRect() {
        return faceRect;
    }
}

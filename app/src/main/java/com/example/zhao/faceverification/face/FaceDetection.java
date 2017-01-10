package com.example.zhao.faceverification.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.zhao.faceverification.R;
import com.example.zhao.faceverification.context.MyApplication;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Face detection with OpenCV and Android API
 *
 * @author zhao
 */
public class FaceDetection {
    public static CascadeClassifier mCascadeClassifier = null;
    public static File mCascadeFile;

    static {
//		OpenCVLoader.initDebug();
        try {
            // load cascade file from application resources
            InputStream is = MyApplication.getContext().getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
            File cascadeDir = MyApplication.getContext().getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mCascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (mCascadeClassifier.empty()) {
                Log.e("main", "Failed to load cascade classifier");
                mCascadeClassifier = null;
            } else
                Log.i("main", "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("main", "Failed to load cascade. Exception thrown: " + e);
        }
    }

    /**
     * Face detection with OpenCV
     *
     * @author zhao
     */
    public static FaceWrapper faceDetectWithOpenCV(Bitmap image) {
        long t = System.currentTimeMillis();
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        Utils.bitmapToMat(image, rgbMat);
//		Imgproc.resize(rgbMat,rgbMat,new Size(640,480));
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGBA2GRAY);

        if (mCascadeClassifier.empty()) {
            Log.d("main", "Cascade can not loaded");
        }

        MatOfRect faces = new MatOfRect();

        Imgproc.equalizeHist(grayMat, grayMat);
        mCascadeClassifier.detectMultiScale(grayMat, faces, 1.1, 2, 2, new Size(100, 100), new Size(400, 400));
        Rect[] rectFaces = faces.toArray();
        Log.d("detection time", String.valueOf(System.currentTimeMillis() - t));
        Log.d("live", "OpenCV faces " + rectFaces.length);
        if (rectFaces.length > 0) {
            Log.d("live", "检测到人脸");
            Rect maxRect = findMaxRect(rectFaces);
            Mat faceMat = new Mat(rgbMat, maxRect);
            Mat xmat = new Mat();
            Imgproc.resize(faceMat, xmat, new Size(128, 128));
            Bitmap m = Bitmap.createBitmap(xmat.cols(), xmat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(xmat, m);
            FaceWrapper faceWrapper = new FaceWrapper(m, maxRect);
            return faceWrapper;
        } else {
            Log.d("live", "没有检测到人脸");
            return null;
        }

    }

    public static Rect findMaxRect(Rect[] rects) {
        Rect maxRect = rects[0];
        for (int i = 0; i < rects.length; i++) {
            if (rects[i].height * rects[i].width > maxRect.height * maxRect.width) {
                maxRect = rects[i];
            }
        }
        return maxRect;
    }

}

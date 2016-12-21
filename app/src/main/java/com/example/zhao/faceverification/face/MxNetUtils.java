package com.example.zhao.faceverification.face;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.zhao.faceverification.face.FacePredictor;

import org.dmlc.mxnet.Predictor;

import java.nio.ByteBuffer;

/**
 * Created by zhao on 2016/11/17.
 */
public class MxNetUtils {

    private MxNetUtils() {
    }

    public static float identifyImage(final Bitmap srcBitmap, final Bitmap dstBitmap) {

        float[] srcGray = getGrayArray(srcBitmap);
        float[] srcFeatures = getFeatures(srcGray);

        float[] dstGray = getGrayArray(dstBitmap);
        float[] dstFeatures = getFeatures(dstGray);

        float s = calCosineSimilarity(srcFeatures, dstFeatures);
        Log.d("main","similarity"+s);
        return s;
    }

    public static float[] getGrayArray(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] bytes = byteBuffer.array();

        float[] gray = new float[128 * 128];

        for (int i = 0; i < bytes.length; i += 4) {
            int j = i / 4;
            float r = (float) (((int) (bytes[i + 0])) & 0xFF);
            float g = (float) (((int) (bytes[i + 1])) & 0xFF);
            float b = (float) (((int) (bytes[i + 2])) & 0xFF);

            int temp = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            gray[j] = (float) (temp / 255.0);
        }
        return gray;
    }

    public static float[] getFeatures(float[] gray) {
        Predictor predictor = FacePredictor.getPredictor();
        predictor.forward("data", gray);
        final float[] result = predictor.getOutput(0);
        return result;
    }

    public static float calCosineSimilarity(float[] a, float[] b) {

        if (a.length != b.length) {
            return 0;
        }

        float n = 0;
        float x = 0;
        float y = 0;
        for (int i = 0; i < a.length; i++) {
            n += a[i] * b[i];
            x += a[i] * a[i];
            y += b[i] * b[i];
        }
        float s = (float) (n / (Math.sqrt(x) * Math.sqrt(y)));

        return s;
    }

}

package com.example.zhao.faceverification.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.zhao.faceverification.R;
import com.example.zhao.faceverification.context.MyApplication;

import org.dmlc.mxnet.Predictor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhao on 2016/11/17.
 */
public class MxNetUtils {

    private MxNetUtils() {
    }

    public static float identifyImage(final Bitmap srcBitmap, final Bitmap dstBitmap) {

        float[] srcFeatures = getFeatures(srcBitmap);
        float[] dstFeatures = getFeatures(dstBitmap);

        float s = calCosineSimilarity(srcFeatures, dstFeatures);

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

    public static float[] getFeatures(Bitmap srcBitmap) {
        float[] srcGray = getGrayArray(srcBitmap);
        long t = System.currentTimeMillis();
        Predictor predictor = FacePredictor.getPredictor();
        predictor.forward("data", srcGray);
        final float[] result = predictor.getOutput(0);
        Log.d("verification time", String.valueOf(System.currentTimeMillis() - t));
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

        Log.d("main", "similarity" + s);

        return s;
    }

    public static float[] getAligments(final Bitmap FaceBitmap) {
        long t = System.currentTimeMillis();

        ByteBuffer byteBuffer = ByteBuffer.allocate(FaceBitmap.getByteCount());
        FaceBitmap.copyPixelsToBuffer(byteBuffer);
        byte[] bytes = byteBuffer.array();

        List<String> meanText = readRawTextFile(MyApplication.getContext(), R.raw.mean);
        int[] meanInt = listToArray(meanText);

        List<String> stdText = readRawTextFile(MyApplication.getContext(), R.raw.std);
        int[] stdInt = listToArray(stdText);

        float[] colors = new float[3 * 40 * 40];

        for (int i = 0; i < bytes.length; i += 4) {
            int j = i / 4;
            colors[0 * 40 * 40 + j] = (float) (((float) (((int) (bytes[i + 0])) & 0xFF) - (float) (meanInt[3 * j + 0])) / (1e-6 + (float) (stdInt[3 * j + 0])));
            colors[1 * 40 * 40 + j] = (float) (((float) (((int) (bytes[i + 1])) & 0xFF) - (float) (meanInt[3 * j + 1])) / (1e-6 + (float) (stdInt[3 * j + 1])));
            colors[2 * 40 * 40 + j] = (float) (((float) (((int) (bytes[i + 2])) & 0xFF) - (float) (meanInt[3 * j + 2])) / (1e-6 + (float) (stdInt[3 * j + 2])));
        }

        Predictor predictor = FaceAlignment.getPredictor();

        predictor.forward("data", colors);
        float[] temp = predictor.getOutput(0);

        final float[] result = new float[temp.length];

        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] + 0.5f;
        }
        Log.d("align time", String.valueOf(System.currentTimeMillis() - t));
        return result;
    }

    public static int[] listToArray(List<String> list) {
        int[] arrayInt = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            int temp = Integer.parseInt(list.get(i));
            arrayInt[i] = temp;
        }
        return arrayInt;
    }

    public static List<String> readRawTextFile(Context ctx, int resId) {
        List<String> result = new ArrayList<>();
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;

        try {
            while ((line = buffreader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            return null;
        }
        return result;
    }

}

package com.example.zhao.faceverification.face;

import android.content.Context;

import com.example.zhao.faceverification.R;
import com.example.zhao.faceverification.context.MyApplication;

import org.dmlc.mxnet.Predictor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhao on 2016/11/24.
 */
public class FacePredictor {

    private static Predictor predictor;
    public  static byte[] symbol=null;
    public static  byte[] params=null;

    static{
        symbol = readRawFile(MyApplication.getContext(), R.raw.lightened_cnn_android_symbol);
        params = readRawFile(MyApplication.getContext(), R.raw.lightened_cnn_android_params);
    }
    public static Predictor getPredictor() {


        Predictor.Device device = new Predictor.Device(Predictor.Device.Type.CPU, 0);
        final int[] shape = {1, 1,128, 128};
        String key = "data";

        Predictor.InputNode node = new Predictor.InputNode(key, shape);

        predictor = new Predictor(symbol, params, device, new Predictor.InputNode[]{node});

        return predictor;
    }

    public static byte[] readRawFile(Context ctx, int resId)
    {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        int size = 0;
        byte[] buffer = new byte[1024];
        try (InputStream ins = ctx.getResources().openRawResource(resId)) {
            while((size=ins.read(buffer,0,1024))>=0){
                outputStream.write(buffer,0,size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

}

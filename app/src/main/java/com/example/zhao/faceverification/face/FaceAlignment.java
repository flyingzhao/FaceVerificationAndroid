package com.example.zhao.faceverification.face;

import android.content.Context;

import com.example.zhao.faceverification.R;
import com.example.zhao.faceverification.context.MyApplication;

import org.dmlc.mxnet.Predictor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhao on 2016/12/11.
 */
public class FaceAlignment {
    private static Predictor align;
    public  static byte[] symbol=null;
    public static  byte[] params=null;

    static{
        symbol = readRawFile(MyApplication.getContext(), R.raw.vanilla_cnn_android_symbol);
        params = readRawFile(MyApplication.getContext(), R.raw.vanilla_cnn_android_params);
    }
    public static Predictor getPredictor() {

        Predictor.Device device = new Predictor.Device(Predictor.Device.Type.CPU, 0);
        final int[] shape = {1,3, 40,40};
        String key = "data";

        Predictor.InputNode node = new Predictor.InputNode(key, shape);

        align = new Predictor(symbol, params, device, new Predictor.InputNode[]{node});

        return align;
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

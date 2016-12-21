package com.example.zhao.faceverification.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.zhao.faceverification.R;
import com.example.zhao.faceverification.face.FaceDetection;
import com.example.zhao.faceverification.face.MxNetUtils;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= (ImageView) findViewById(R.id.imageView);

        long t=System.currentTimeMillis();
        Bitmap sBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a);
        Bitmap dBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b);

        Bitmap srcFace=FaceDetection.faceDetectWithOpenCV(sBitmap);
        Bitmap dstFace= FaceDetection.faceDetectWithOpenCV(dBitmap);

        float similarity= MxNetUtils.identifyImage(srcFace,dstFace);

        Log.d("verification time", String.valueOf(System.currentTimeMillis()-t));

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


}

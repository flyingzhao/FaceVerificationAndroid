package com.example.zhao.faceverification.face;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by zhao on 2016/12/21.
 */

public class ImageUtils {

//        private static float[] standard_shape={0.2766f,0.1641f,0.7073f,0.1558f,0.4950f,0.5144f,0.3071f,0.6418f,0.6954f,0.6341f,0.498f,0.645f};
    private static float[] standard_shape={0.2766f,0.2241f,0.7073f,0.2241f,0.4950f,0.5144f,0.3071f,0.6418f,0.6954f,0.6341f,0.498f,0.695f};//left eye right eye nose tip mouth corner mouth corner middle mouth

    public static Bitmap getAffineBitmap(float[] landmarks, Bitmap originBitmap, Rect rect){

        float[] newLandmark=new float[landmarks.length];
        for (int i = 0; i < landmarks.length/2; i++) {
            newLandmark[2*i+0]=landmarks[2*i+0]*rect.width+rect.x;
            newLandmark[2*i+1]=landmarks[2*i+1]*rect.height+rect.y;
        }

        float[] newStdShape=new float[standard_shape.length];
        for (int i = 0; i < standard_shape.length; i++) {
            newStdShape[i]=standard_shape[i]*rect.height;
        }

        MatOfPoint2f src=new MatOfPoint2f(new Point(newLandmark[0],newLandmark[1]),new Point(newLandmark[2],newLandmark[3]),new Point((newLandmark[6]+newLandmark[8])/2,(newLandmark[7]+newLandmark[9])/2));
        MatOfPoint2f dst=new MatOfPoint2f(new Point(newStdShape[0],newStdShape[1]),new Point(newStdShape[2],newStdShape[3]),new Point(newStdShape[10],newStdShape[11]));

        Mat affineMatrix= Imgproc.getAffineTransform(src,dst);

        Mat rgbMat = new Mat();
        Utils.bitmapToMat(originBitmap, rgbMat);

        Mat alignFace=new Mat();
        Imgproc.warpAffine(rgbMat,alignFace,affineMatrix,new Size(rect.height,rect.height));

        Bitmap m= Bitmap.createBitmap(alignFace.cols(),alignFace.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(alignFace,m);

        return m;
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    public static Bitmap getAlignedFaceFromImage(Bitmap oBitmap){

        Bitmap sBitmap=getResizedBitmap(oBitmap,480,640);

        FaceWrapper srcFaceWrapper=FaceDetection.faceDetectWithOpenCV(sBitmap);

        if (srcFaceWrapper==null){
            return null;
        }
        Log.d("main","face wrapper"+srcFaceWrapper.getFaceBitmap().getWidth());
        Bitmap srcFace=srcFaceWrapper.getFaceBitmap();
        Rect srcRect=srcFaceWrapper.getFaceRect();

        Bitmap face = getResizedBitmap(srcFace, 40, 40);

        float[] landmarks=MxNetUtils.getAligments(face);

        Bitmap affineBitmap=getAffineBitmap(landmarks,sBitmap,srcRect);

        return getResizedBitmap(affineBitmap,128,128);
    }


}

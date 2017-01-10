package com.example.zhao.faceverification.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhao.faceverification.R;
import com.example.zhao.faceverification.face.ImageUtils;
import com.example.zhao.faceverification.face.MxNetUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CAPTURE_PHOTO_CODE = 1;
    private static float[] features = null;
    private TextView resultTextView;
    private ImageView inputImageView;
    private Bitmap bitmap;
    private Bitmap processedBitmap;
    private Button registerButton;
    private Button verifyButton;
    private Button resetButton;
    private String currentPhotoPath;

    private Bitmap processBitmap(final Bitmap origin) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        int newWidth = 480;
        int newHeight = 640;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                origin, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                if (processedBitmap == null) {
                    Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AsyncTask<Bitmap, Void, Boolean>() {
                    @Override
                    protected void onPreExecute() {

                        resultTextView.setText("Calculating...");
                    }

                    @Override
                    protected Boolean doInBackground(Bitmap... bitmaps) {
                        synchronized (registerButton) {

                            Bitmap sFace = ImageUtils.getAlignedFaceFromImage(bitmaps[0]);
                            if (sFace == null) {
                                return false;
                            }
                            features = MxNetUtils.getFeatures(sFace);
                            return true;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean tag) {
                        if (tag) {
                            resultTextView.setText("register successfully");
                            bitmap = null;
                            processedBitmap = null;
                            registerButton.setVisibility(View.INVISIBLE);
                            verifyButton.setVisibility(View.VISIBLE);
                            resetButton.setVisibility(View.VISIBLE);
                            Bitmap addBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.round_add);
                            inputImageView.setImageBitmap(addBitmap);
                        } else {
                            resultTextView.setText("register fail");
                            bitmap = null;
                            processedBitmap = null;
                            registerButton.setVisibility(View.VISIBLE);
                            verifyButton.setVisibility(View.INVISIBLE);
                            resetButton.setVisibility(View.INVISIBLE);
                            Bitmap addBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.round_add);
                            inputImageView.setImageBitmap(addBitmap);
                        }

                    }
                }.execute(processedBitmap);

                break;
            case R.id.tap_to_add_image:
                dispatchTakePictureIntent();
                break;

            case R.id.verify_button:
                if (processedBitmap == null) {
                    Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AsyncTask<Bitmap, Void, Float>() {
                    @Override
                    protected void onPreExecute() {
                        resultTextView.setText("Calculating...");
                    }

                    @Override
                    protected Float doInBackground(Bitmap... bitmaps) {
                        synchronized (verifyButton) {
                            Bitmap sFace = ImageUtils.getAlignedFaceFromImage(bitmaps[0]);
                            if (sFace == null) {
                                return 0f;
                            }
                            float[] faceFeatures = MxNetUtils.getFeatures(sFace);
                            float s = MxNetUtils.calCosineSimilarity(features, faceFeatures);
                            return s;
                        }
                    }

                    @Override
                    protected void onPostExecute(Float s) {
                        String showSim = String.format("%.4f", s);
                        if (s > 0.5) {
                            resultTextView.setText("verify success " + showSim);
                            Bitmap addBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.round_add);
                            inputImageView.setImageBitmap(addBitmap);
                            bitmap = null;
                            processedBitmap = null;
                        } else {
                            resultTextView.setText("verify fail " + showSim);
                            Bitmap addBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.round_add);
                            inputImageView.setImageBitmap(addBitmap);
                            bitmap = null;
                            processedBitmap = null;
                        }
                    }
                }.execute(processedBitmap);
                break;
            case R.id.reset_button:
                bitmap = null;
                processedBitmap = null;
                features = null;
                registerButton.setVisibility(View.VISIBLE);
                verifyButton.setVisibility(View.INVISIBLE);
                resetButton.setVisibility(View.INVISIBLE);
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        long t = System.currentTimeMillis();
//        Bitmap srcOriginImage = BitmapFactory.decodeResource(getResources(), R.drawable.a);
//        Bitmap dstOriginImage = BitmapFactory.decodeResource(getResources(), R.drawable.b);
//
//        Bitmap sFace = ImageUtils.getAlignedFaceFromImage(srcOriginImage);
//        Bitmap dFace = ImageUtils.getAlignedFaceFromImage(dstOriginImage);
//
//        float s = MxNetUtils.identifyImage(sFace, dFace);
//        Log.d("total time", String.valueOf(System.currentTimeMillis() - t));

        registerButton = (Button) findViewById(R.id.register_button);
        verifyButton = (Button) findViewById(R.id.verify_button);
        resetButton = (Button) findViewById(R.id.reset_button);
        inputImageView = (ImageView) findViewById(R.id.tap_to_add_image);
        resultTextView = (TextView) findViewById(R.id.result_text);

        verifyButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);

        registerButton.setOnClickListener(this);
        inputImageView.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(takePictureIntent, CAPTURE_PHOTO_CODE);
            }
        }
    }


    //sharedPreferences.edit().putBoolean(PREF_REGISTER_KEY, false).apply();

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case CAPTURE_PHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    processedBitmap = processBitmap(bitmap);
                    inputImageView.setImageBitmap(bitmap);
                }
                break;
        }
    }


}

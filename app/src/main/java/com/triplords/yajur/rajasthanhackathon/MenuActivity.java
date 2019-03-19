package com.triplords.yajur.rajasthanhackathon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MenuActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private Vision vision;
    private Feature feature;
    private Bitmap bitmap;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int RECORD_REQUEST_CODE = 101;
    private String api = "LABEL_DETECTION";
    private String captiony;
    private ImageView IV1;
    private TextToSpeech tts;
    private FrameLayout flLoad;
    private VisionServiceClient client;
    private EditText mEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        tts = new TextToSpeech(this,this);

        mEditText = (EditText)findViewById(R.id.editText2);
        flLoad=(FrameLayout)this.findViewById(R.id.loadfl);
        flLoad.setVisibility(View.VISIBLE);
        initialize();
        if (client==null){
            client = new VisionServiceRestClient(getString(R.string.subscription_key), getString(R.string.subscription_apiroot));
        }

    }

    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence
            //Log.d("yoyo", "123456");
            flLoad.setVisibility(View.INVISIBLE);
            mEditText.setText("");
            if (e != null) {
                mEditText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

               // mEditText.append("Image format: " + result.metadata.format + "\n");
                //mEditText.append("Image width: " + result.metadata.width + ", height:" + result.metadata.height + "\n");
                //mEditText.append("\n");

                for (Caption caption: result.description.captions) {
                    mEditText.append(caption.text + "\n");
                }
                captiony = mEditText.getText().toString();

                tts.speak(captiony, TextToSpeech.QUEUE_FLUSH, null, null);
                /*
                caption.confidence +
                mEditText.append("\n");

                for (String tag: result.description.tags) {
                    mEditText.append("Tag: " + tag + "\n");
                }
                mEditText.append("\n");

                mEditText.append("\n--- Raw Data ---\n\n");
                mEditText.append(data);
                mEditText.setSelection(0);
                */
            }

        }
    }


    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.describe(inputStream, 1);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }
    public void doDescribe() {
        mEditText.setText("Describing...");

        try {
            new doRequest().execute();
        } catch (Exception e)
        {
            mEditText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    @Override
    public void onInit(int status) {

        if(status == TextToSpeech.SUCCESS)
        {
            int result = tts.setLanguage(Locale.US);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                Log.e("err","lang unsupported");
        }
        else
            Log.e("err","Init Failed");
    }



    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

           // mImageUri = data.getData();

            bitmap = (Bitmap) data.getExtras().get("data");
            IV1.setImageBitmap(bitmap);
            //callCloudVision(bitmap, feature);

            doDescribe();
        }
    }

    private void initialize()
    {

        IV1 = (ImageView)this.findViewById(R.id.iv1);
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyBRtN8i0QzYXmsxkoWwbWlyzgH8uRg9r9k"));
        vision = visionBuilder.build();


        feature = new Feature();
        feature.setType("LABEL_DETECTION");
        feature.setMaxResults(3);

        takePictureFromCamera();

    }
    private int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flLoad.setVisibility(View.VISIBLE);
        if (!(checkPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            makeRequest(Manifest.permission.CAMERA);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            }
        }
    }








}


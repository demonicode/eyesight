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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MenuActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextView tvv1,tvv2,tvv3;
    private Vision vision;
    private Feature feature;
    private Bitmap bitmap;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int RECORD_REQUEST_CODE = 101;
    private String api = "LABEL_DETECTION";
    private ImageView IV1;
    private TextToSpeech tts;
    private FrameLayout flLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        tts = new TextToSpeech(this,this);
        tvv1=(TextView)this.findViewById(R.id.tvv1);
        tvv2=(TextView)this.findViewById(R.id.tvv2);
        tvv3=(TextView)this.findViewById(R.id.tvv3);
        flLoad=(FrameLayout)this.findViewById(R.id.loadfl);
        flLoad.setVisibility(View.VISIBLE);
        initialize();

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

    private void speakOut()
    {
        String text="";
        if(tvv1.getText()!=null) {
            text += tvv1.getText().toString();
        }
        if(tvv2.getText()!=null) {
            text += ", "+tvv1.getText().toString();
        }
        if(tvv3.getText()!=null) {
            text += "and "+tvv1.getText().toString()+ " describe your surroundings.";
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            IV1.setImageBitmap(bitmap);
            callCloudVision(bitmap, feature);
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



    private void callCloudVision(final Bitmap bitmap, final Feature feature)
    {
        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);
        new AsyncTask<Object, Void, String[]>() {
            @Override
            protected String[] doInBackground(Object... params) {
                try {
                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer("AIzaSyBRtN8i0QzYXmsxkoWwbWlyzgH8uRg9r9k");

                    Vision.Builder builder = new Vision.Builder(
                            new NetHttpTransport(),
                            new AndroidJsonFactory(),
                            null);
                    builder.setVisionRequestInitializer(requestInitializer);
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d("tagtag", "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d("tagtag", "failed to make API request because of other IOException " + e.getMessage());
                }
                return null;
            }

            protected void onPostExecute(String[] result) {
                //int j=0;
                //for(j=0;j<result.length;j++)
                //Log.v("resultResult",result[j]);
                flLoad.setVisibility(View.GONE);
                tvv1.setText(result[0]);
                tvv2.setText(result[1]);
                tvv3.setText(result[2]);
                speakOut();
            }
        }.execute();
    }


    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }


    private String[] convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);
        List<EntityAnnotation> entityAnnotations;

        String message[] = {};
        switch (api) {
            case "LABEL_DETECTION":
                entityAnnotations = imageResponses.getLabelAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;
        }
        return message;
    }



    private String[] formatAnnotation(List<EntityAnnotation> entityAnnotation) {
        String[] message = {"","",""};
        String i; int j=0;

        if (entityAnnotation != null) {
            for (EntityAnnotation entity : entityAnnotation) {
                i =entity.getDescription();
                message[j]=i;
                j++;
            }
        }
        return message;
    }


}


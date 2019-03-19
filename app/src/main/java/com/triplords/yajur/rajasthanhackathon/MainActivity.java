package com.triplords.yajur.rajasthanhackathon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CameraDetector.CameraEventListener, Detector.FaceListener, Detector.ImageListener, TextToSpeech.OnInitListener
{
    private CameraDetector detector = null;
    private RelativeLayout mrl;
    private SurfaceView cameraView;
    private FrameLayout fl1;
    int cameraPreWidth=0;
    int cameraPreHeight=0;
    private boolean cameraPermissionsAvailable = false;
    private boolean storagePermissionsAvailable = false;
    private TextView tv;
    private TextView tv2;
    private TextToSpeech tts;
    private TextView tv3;
    private int curr=0;
    boolean first;

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        first = true;
        //startActivity(new Intent(this,ActualMenu.class));
        tv = (TextView) this.findViewById(R.id.textView);
        tts = new TextToSpeech(this,this);
        tv2=(TextView)this.findViewById(R.id.textView2);
        tv3=(TextView)this.findViewById(R.id.textView3);
        fl1=(FrameLayout)this.findViewById(R.id.fl1);
        fl1.setVisibility(View.VISIBLE);
        mrl = (RelativeLayout) (this.findViewById(R.id.mrl));
        cameraView = (SurfaceView) (this.findViewById(R.id.camera_viewz));
        checkForCameraPermissions();

    }

    @Override
    public void onInit(int status) {

        if(status == TextToSpeech.SUCCESS)
        {
            int result = tts.setLanguage(Locale.US);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            Log.e("err","lang unsupported");
            else {
                speakOut();
            }
        }
        else
            Log.e("err","Init Failed");
    }

    private void speakOut()
    {
        String text;
        if(tv.getText()!=null) {
            text = tv.getText().toString();
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onFaceDetectionStarted() {}

    @Override
    public void onFaceDetectionStopped(){
        //speakOut();
    }

    @Override
    public void onImageResults(List<Face> faces, Frame image, float timeStamp)
    {
        fl1.setVisibility(View.GONE);
     if (faces==null)
         return;
        if (faces.size()==0)
            return;
        for( int i = 0; i <faces.size(); i++) {
            Face face = faces.get(i);

            Face.GENDER genderValue = face.appearance.getGender();
            Face.GLASSES glassesValue = face.appearance.getGlasses();
            Face.AGE ageValue = face.appearance.getAge();


            float joy = face.emotions.getJoy();
            float anger = face.emotions.getAnger();
            float surprise = face.emotions.getSurprise();
            float sadness = face.emotions.getSadness();


            int a = face.getFacePoints().length;
            PointF aa [] = face.getFacePoints();

            for(int ii =0 ; (ii < a) && first ; ii++)
            {

                Log.v("xxxxxx",String.valueOf(aa[ii].x));
                if (ii==a-1) {
                    first = false;
                    for(ii =0 ; (ii < a) ; ii++)
                    {

                        Log.v("yyyyy",String.valueOf(aa[ii].y));
                        if (ii==a-1)
                            first = false;
                    }
                }
            }



            if (anger > 50.00) {
                tv.setText("Angry!");
                if(curr!=1)
                speakOut();
                curr=1;
            }

            else if (sadness > 45.00) {
                tv.setText("Sad");
                if(curr!=2)
                speakOut();
                curr=2;
            }

            else if (surprise > 38.00) {
                tv.setText("Surprised");
                if(curr!=3)
                speakOut();
                curr=3;
            }

            else if (joy > 80.00) {
                tv.setText("Happy!");
                if(curr!=4)
                speakOut();
                curr=4;
            }

            else {
                tv.setText("Normal Expression");
                curr=0;
            }

            if (genderValue.equals(Face.GENDER.FEMALE))
                tv2.setText("Female ");
            else if (genderValue.equals(Face.GENDER.MALE))
                tv2.setText("Male ");
            else
                tv2.setText("");
            String av = "Unknown";
            if (!ageValue.equals(Face.AGE.AGE_UNKNOWN)) {

                if (ageValue.equals(Face.AGE.AGE_UNDER_18))
                    av = " <18";
                else if (ageValue.equals(Face.AGE.AGE_18_24))
                    av = "18-24";
                else if (ageValue.equals(Face.AGE.AGE_25_34))
                    av = "25-34";
                else if (ageValue.equals(Face.AGE.AGE_35_44))
                    av = "35-44";
                else if (ageValue.equals(Face.AGE.AGE_45_54))
                    av = "45-54";
                else if (ageValue.equals(Face.AGE.AGE_55_64))
                    av = "55-64";
                else if (ageValue.equals(Face.AGE.AGE_65_PLUS))
                    av = ">65";
                String s = tv2.getText().toString();
                tv2.setText(s);
            } else
            {
                if(genderValue.equals(Face.GENDER.FEMALE))
                    tv2.setText("Female ");
                else if(genderValue.equals(Face.GENDER.MALE))
                    tv2.setText("Male ");
                else
                    tv2.setText("");
            }
            if(glassesValue.equals(Face.GLASSES.NO))
                tv3.setText("No Glasses");
            if(glassesValue.equals(Face.GLASSES.YES))
                tv3.setText("Wearing Glasses");



        }



    }



    private void checkForCameraPermissions() {
        fl1.setVisibility(View.VISIBLE);
        cameraPermissionsAvailable =
                ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        storagePermissionsAvailable =
                ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(!storagePermissionsAvailable)
            requestStoragePermissions();

        if (!cameraPermissionsAvailable)
            requestCameraPermissions();

        if(storagePermissionsAvailable&&cameraPermissionsAvailable)
            initCamDetector();
        /*if (!cameraPermissionsAvailable) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Log.v("abcabc","checkcamera 2");
                showPermissionExplanationDialog(42);
            } else {
                Log.v("abcabc","checkcamera 3");
                requestCameraPermissions();
            }
        } */
    }


    private void requestCameraPermissions() {
        if (!cameraPermissionsAvailable) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    42);

        }
    }
    private void requestStoragePermissions() {
        if (!storagePermissionsAvailable) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    73);

        }
    }



    private void initCamDetector()
    {
        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraView, 1 , Detector.FaceDetectorMode.LARGE_FACES);
        detector.setSendUnprocessedFrames(true);
        detector.setFaceListener(this);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        detector.setDetectAllExpressions(true);
        detector.setDetectAllAppearances(true);
        detector.setDetectAllEmotions(true);
        detector.start();
    }

    @Override
    public void onCameraSizeSelected(int i, int i1, Frame.ROTATE rotate) {


        if(rotate == Frame.ROTATE.BY_90_CCW || rotate == Frame.ROTATE.BY_90_CW) {
            cameraPreHeight = i;
            cameraPreWidth = i1;
        }
        else {
            cameraPreHeight = i1;
            cameraPreWidth = i;
        }

        mrl.post(new Runnable() {
            @Override
            public void run() {

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

                int lWidth = displaymetrics.widthPixels;
                int lHeight = displaymetrics.heightPixels;

                if (cameraPreWidth == 0 || cameraPreHeight == 0 || lWidth == 0 || lHeight == 0)
                    return;

                float layoutAspectRatio = (float) lWidth / lHeight;
                float cameraPreviewAspectRatio = (float) cameraPreWidth / cameraPreHeight;

                int newWidth;
                int newHeight;

                if (cameraPreviewAspectRatio > layoutAspectRatio) {
                    newWidth = lWidth;
                    newHeight = (int) (lWidth / cameraPreviewAspectRatio);
                } else {
                    newWidth = (int) (lHeight * cameraPreviewAspectRatio);
                    newHeight = lHeight;
                }


                ViewGroup.LayoutParams params = mrl.getLayoutParams();
                params.height = newHeight;
                params.width = newWidth;
                mrl.setLayoutParams(params);


               // progressBarLayout.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 42) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA)) {
                    cameraPermissionsAvailable = (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }


        }

        if (requestCode == 73) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    storagePermissionsAvailable = (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }

        }

        if (cameraPermissionsAvailable&&storagePermissionsAvailable) {
            initCamDetector();
        }

    }

}

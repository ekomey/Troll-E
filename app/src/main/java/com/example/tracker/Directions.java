package com.example.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import java.io.File;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;


//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Directions extends AppCompatActivity implements SensorEventListener {
    // Sensors
    private Sensors sensors;

    // Database
    private float rMat[] = new float[9];
    private float[] orientation = new float[3];

    private Thread thread;
    private boolean plotData = true;

    private static final String TAG = Directions.class.getSimpleName();
    private float compass;
    private float initialCompass = -1;
    private float compassDiff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.sensors = new Sensors(this);

        feedMultiple();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
    }

    @Override
    protected void onStart(){
        super.onStart();
        sensors.sensorStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
        sensors.sensorStop();
    }

    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType){
            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(rMat,event.values);
                compass = Math.round( (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0]) + 360) % 360);

                if (initialCompass < 0){
                    initialCompass = compass;
                }
                compassDiff = compass - initialCompass;

                Log.d(TAG, "Compassdiff: "+ compassDiff);
                Log.d(TAG, "InitialCompass: "+ initialCompass);
                Log.d(TAG, "Compass: "+ compass);

                //down
                if ((compassDiff>=137 && compassDiff<=223) || (compassDiff<=-137 && compassDiff>=-223)){
                    final Animation animation1 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation1.setDuration(500); // duration - half a second
                    animation1.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation1.setRepeatCount(4); // Repeat animation infinitely
                    animation1.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    final ImageButton btn1 = findViewById(R.id.imageDown);
                    btn1.startAnimation(animation1);
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.clearAnimation();

                        }
                    });

                    initialCompass += 180;
                    if (initialCompass>=360){
                        initialCompass -= 360;
                    }
                }

                //right
                else if ((compassDiff>=47 && compassDiff<=133) || (compassDiff<=-227 && compassDiff>=-313)){
                    final Animation animation2 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation2.setDuration(500); // duration - half a second
                    animation2.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation2.setRepeatCount(3); // Repeat animation infinitely
                    animation2.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    final ImageButton btn2 = findViewById(R.id.imageRight);
                    btn2.startAnimation(animation2);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.clearAnimation();
                        }
                    });

                    initialCompass += 90;
                    if (initialCompass >= 360){
                        initialCompass -= 360;
                    }
                }

                //left
                else if ((compassDiff>=-137 && compassDiff<=-43) || (compassDiff<=313 && compassDiff>=227)){
                    final Animation animation3 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation3.setDuration(500); // duration - half a second
                    animation3.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation3.setRepeatCount(3); // Repeat animation infinitely
                    animation3.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    final ImageButton btn3 = findViewById(R.id.imageLeft);
                    btn3.startAnimation(animation3);
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.clearAnimation();
                        }
                    });

                    initialCompass -= 90;
                    if (initialCompass < 0){
                        initialCompass += 360;
                    }
                }

                //up
                else if ((compassDiff>=-43) || (compassDiff<=43)){
                    final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation.setDuration(500); // duration - half a second
                    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation.setRepeatCount(3); // Repeat animation infinitely
                    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    final ImageButton btn = findViewById(R.id.imageUp);
                    btn.startAnimation(animation);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.clearAnimation();
                        }
                    });
                }

                break;

                default :
                    break;
        }
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        sensors.sensorPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensors.sensorResume();
    }

    @Override
    protected void onDestroy() {
        sensors.sensorDestroy();
        thread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
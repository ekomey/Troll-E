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

    // Values in app
    private TextView mTextSensorAccelerometer;
    private TextView mTextSensorGyroscope;
    private TextView mTextSensorOrientation;
    private TextView mTextVelocity;
    private TextView mTextVelocityGPS;
    private TextView mTextVelocityKMH;

    // Graph
    //private Charts charts;

    // Database
    private float rMat[] = new float[9];
    private float[] orientation = new float[3];
    // Assume device not moving when starting
    private double velocityX = 0.0f;
    private double velocityY = 0.0f;
    private double velocityZ = 0.0f;
    private double timestamp = 0.0f;
    // Convert nanoseconds to seconds
    private static final float nanosecond2second = 1.0f / 1000000000.0f;

    // Variables to store data retrieved from sensor
    private float xValue, yValue, zValue;

    private Thread thread;
    private boolean plotData = true;

    // Writing sensor data to file
    private File fileDir, file;
    private TextView tv;
    private Context context;

    // Buttons to start and stop
    private Button startButton;
    private Button stopButton;
    private boolean record = false;

    private static final String TAG = Directions.class.getSimpleName();
    private float compass;
    private float initialCompass = -1;
    private float compassDiff;

    // Server related
//    private FirebaseDatabase mFirebaseDatabase;
//    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // Get database info
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mDatabase = mFirebaseDatabase.getReference("data");

        this.sensors = new Sensors(this);
//        this.charts = new Charts(this);

        // Access text in the app.
//        mTextSensorAccelerometer = (TextView) findViewById(R.id.label_accelerometer);
//        mTextSensorOrientation = (TextView) findViewById(R.id.label_compass);
//        mTextVelocity = (TextView) findViewById(R.id.label_velocity);
//        //mTextVelocityGPS = (TextView) findViewById((R.id.label_velocity_GPS));
//        mTextVelocityKMH = (TextView) findViewById(R.id.label_velocity_KMH);

        feedMultiple();

        // get IDs of buttons
//        startButton = findViewById(R.id.startButton);
//        stopButton = findViewById(R.id.stopButton);
//        context = this.getApplicationContext();

//        startButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                record = true;
//                startButton.setEnabled(false);
//
//                /*
//                 * Data logging part, create/check for file.
//                 * Very rough implementation, can possibly be improved.
//                 * */
//                String currentTime = new SimpleDateFormat("HH.mm.ss", Locale.getDefault()).format(new Date());
//                String pathName = context.getExternalFilesDir(null) + "/" + "AccelLog " + currentTime + ".csv";
//                file = new File(pathName); // Create subfolder + text file
//
//                if(file.exists()){
//                    Log.v("fileDir ", "Exists");
//                    Log.v("fileDir ", context.getExternalFilesDir(null)+ "***");
//                }
//                else {
//                    try {
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.v("fileDir ", "Not created");
//                    }
//                }
//
//                // Show in app for debugging purposes. Can be removed if direct access to csv is possible
//                tv = (TextView)findViewById(R.id.text_view);
//                // End of datalogging part
//            }
//        });
//
//        stopButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                record = false;
//                startButton.setEnabled(true);
//            }
//        });


        //
        //
        //
        //
        //ui part
        //int xValue;
        //xValue = Math.round( (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0]) + 360) % 360);

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
            case Sensor.TYPE_LINEAR_ACCELERATION :
                double deltaTime;

                xValue = event.values[0];
                yValue = event.values[1];
                zValue = event.values[2];

                /*
                 *  Filter small movements to reduce noise.
                 *  If accelerometer values ALL read between -0.1 and 0.1,
                 *  assume the user is simply standing/not moving
                 *  Value should be adjusted later on after further testing
                 */
                if(Math.abs(xValue) <= 0.1 && Math.abs(yValue) <= 0.1 && Math.abs(zValue) <= 0.1)
                {
                    // Movements below 0.1 threshold wont be considered
                    xValue = 0;
                    yValue = 0;
                    zValue = 0;
                }

                // Set the text in the app
//                mTextSensorAccelerometer.setText(getResources().getString(R.string.label_accelerometer, xValue, yValue, zValue));

//                addEntry(event, charts.mChartAccel);

                // Timestamp returns time in nanoseconds, which should be much more accurate
                if(timestamp == 0)
                {
                    // This is the time passed since the last reading. It needs to be 0 for the very first reading
                    deltaTime = 0;
                }
                else
                {
                    deltaTime = (event.timestamp - timestamp) * nanosecond2second;
                }

                timestamp = event.timestamp;

                /*  V0 = V + AT
                 *  A = Acceleration, T = Time in seconds
                 *  T = time since previous reading
                 *  V0 = Previously calculated Velocity. Assume initial velocity is 0m/s.
                 */

                velocityX = (velocityX + (xValue*deltaTime));
                velocityY = (velocityY + (yValue*deltaTime));
                velocityZ = (velocityZ + (zValue*deltaTime));

                /* Note :
                 * Consider using GPS data along with accelerometer data (sensor fusion).
                 * Accelerometer on it's own may not be accurate enough
                 */
//                mTextVelocity.setText(getResources().getString(R.string.label_velocity, velocityX, velocityY, velocityZ));
//                //mTextVelocityGPS.setText(getResources().getString(R.string.label_velocity, velocityX, velocityY, velocityZ));
//                mTextVelocityKMH.setText(getResources().getString(R.string.label_velocity_KMH, velocityX*3.6, velocityY*3.6, velocityZ*3.6));

                /*
                 *  BELOW
                 *  Writing to AccelLog.csv
                 *  Very rough implementation, can possibly be improved.
                 * */
//                if (record == true) {
//                    mDatabase.child("X").setValue(xValue);
//                    mDatabase.child("Y").setValue(yValue);
//                    mDatabase.child("Z").setValue(zValue);
//                }
//                break;
                break;

            case Sensor.TYPE_GYROSCOPE :
                setValues(event);
                //mTextSensorGyroscope.setText(getResources().getString(R.string.label_gyroscope, xValue, yValue, zValue));
//                addEntry(event, charts.mChartGyro);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                setValues (event);
//                addEntry(event, charts.mChartMagneto);
                break;

            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(rMat,event.values);
                compass = Math.round( (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0]) + 360) % 360);
                //mTextSensorOrientation.setText("Compass : " + Float.toString(compass) + (char) 0x00B0);

//                compass = Math.round(event.values[0]);
//                mTextSensorOrientation.setText("Compass : " + Float.toString(compass) + (char) 0x00B0);

//                if (initialCompass < 0){
//                    initialCompass = compass;
//                }
//                compassDiff = compass - initialCompass;
//                compassDiff = Math.abs(compassDiff);

                if (compass>=310 || compass<=50) {
                    final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation.setDuration(500); // duration - half a second
                    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation.setRepeatCount(4); // Repeat animation infinitely
                    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    final ImageButton btn = (ImageButton) findViewById(R.id.imageUp);
                    btn.startAnimation(animation);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.clearAnimation();

                        }
                    });}
                else if (compass>=130 && compass<=220) {
                    final Animation animation1 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation1.setDuration(500); // duration - half a second
                    animation1.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation1.setRepeatCount(4); // Repeat animation infinitely
                    animation1.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    final ImageButton btn1 = (ImageButton) findViewById(R.id.imageDown);
                    btn1.startAnimation(animation1);
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.clearAnimation();

                        }
                    });
                }

                else if (compass>=50 && compass<=130){
                    final Animation animation2 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation2.setDuration(500); // duration - half a second
                    animation2.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation2.setRepeatCount(3); // Repeat animation infinitely
                    animation2.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    final ImageButton btn2 = (ImageButton) findViewById(R.id.imageRight);
                    btn2.startAnimation(animation2);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.clearAnimation();

                        }
                    });
                }
                else if (compass>=220 && compass<=310){
                    final Animation animation3 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation3.setDuration(500); // duration - half a second
                    animation3.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation3.setRepeatCount(3); // Repeat animation infinitely
                    animation3.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    final ImageButton btn3 = (ImageButton) findViewById(R.id.imageLeft);
                    btn3.startAnimation(animation3);
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.clearAnimation();

                        }
                    });
                }

                break;
//                if (record == true) {
//                    mDatabase.child("compass").setValue(xValue);
//                }
//                break;

                default :
                    break;
        }

//        if (xValue>=310 || xValue<=50) {
//            final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
//            animation.setDuration(500); // duration - half a second
//            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
//            animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
//            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
//            final ImageButton btn = (ImageButton) findViewById(R.id.imageUp);
//            btn.startAnimation(animation);
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    view.clearAnimation();
//
//                }
//            });}
//        else if (xValue>=130 && xValue<=220) {
//            final Animation animation1 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
//            animation1.setDuration(500); // duration - half a second
//            animation1.setInterpolator(new LinearInterpolator()); // do not alter animation rate
//            animation1.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
//            animation1.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
//            final ImageButton btn1 = (ImageButton) findViewById(R.id.imageDown);
//            btn1.startAnimation(animation1);
//            btn1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    view.clearAnimation();
//
//                }
//            });
//        }
//
//        else if (xValue>=50 && xValue<=130){
//            final Animation animation2 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
//            animation2.setDuration(500); // duration - half a second
//            animation2.setInterpolator(new LinearInterpolator()); // do not alter animation rate
//            animation2.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
//            animation2.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
//            final ImageButton btn2 = (ImageButton) findViewById(R.id.imageRight);
//            btn2.startAnimation(animation2);
//            btn2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    view.clearAnimation();
//
//                }
//            });
//        }
//        else if (xValue>=220 && xValue<=310){
//            final Animation animation3 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
//            animation3.setDuration(500); // duration - half a second
//            animation3.setInterpolator(new LinearInterpolator()); // do not alter animation rate
//            animation3.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
//            animation3.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
//            final ImageButton btn3 = (ImageButton) findViewById(R.id.imageLeft);
//            btn3.startAnimation(animation3);
//            btn3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    view.clearAnimation();
//
//                }
//            });
//        }
    }

    public void setValues (SensorEvent event) {
        xValue = event.values[0];
        yValue = event.values[1];
        zValue = event.values[2];
    }

//    private void addEntry(SensorEvent event, LineChart chart) {
//
//        LineData data = chart.getData();
//
//        if (data != null) {
//
//            ILineDataSet setX = data.getDataSetByIndex(0);
//            ILineDataSet setY = data.getDataSetByIndex(1);
//            ILineDataSet setZ = data.getDataSetByIndex(2);
//            // set.addEntry(...); // can be called as well
//
//            if (setX == null) {
//                setX = createSet(Color.RED, "X");
//                data.addDataSet(setX);
//            }
//            if (setY == null) {
//                setY = createSet(Color.GREEN, "Y");
//                data.addDataSet(setY);
//            }
//            if (setZ == null) {
//                setZ = createSet(Color.BLUE, "Z");
//                data.addDataSet(setZ);
//            }
//
//            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
//            data.addEntry(new Entry(setX.getEntryCount(), event.values[0]), 0);
//            data.addEntry(new Entry(setY.getEntryCount(), event.values[1]), 1);
//            data.addEntry(new Entry(setZ.getEntryCount(), event.values[2]), 2);
//            data.notifyDataChanged();
//
//            // let the chart know it's data has changed
//            chart.notifyDataSetChanged();
//
//            // limit the number of visible entries
//            chart.setVisibleXRangeMaximum(150);
//            // mChart.setVisibleYRange(30, AxisDependency.LEFT);
//
//            // move to the latest entry
//            chart.moveViewToX(data.getEntryCount());
//        }
//    }

//    private LineDataSet createSet(int colorID, String label) {
//
//        LineDataSet set = new LineDataSet(null, label);
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setLineWidth(3f);
//        set.setColor(colorID);
//        set.setHighlightEnabled(false);
//        set.setDrawValues(false);
//        set.setDrawCircles(false);
//        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        set.setCubicIntensity(0.2f);
//        return set;
//    }

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
    public TextView getmTextSensorAccelerometer() {
        return mTextSensorAccelerometer;
    }

    public TextView getmTextSensorGyroscope() {
        return mTextSensorGyroscope;
    }

    public TextView getmTextSensorOrientation() {
        return mTextSensorOrientation;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
// Code written by Group 9A

package com.example.tracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.hardware.SensorEvent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Directions extends AppCompatActivity implements SensorEventListener {
    private ImageButton helpButton;
    private TextView HelpAlert;

    // Sensors
    private Sensors sensors;
    private Thread thread;

    // Database
    private float rMat[] = new float[9];
    private float[] orientation = new float[3];

    // Compass related
    private static final String TAG = Directions.class.getSimpleName();
    private float compass;
    private float initialCompass = -1;
    private float compassDiff;
    // Movement detection variable
    private float xValue = 0, yValue = 0;
    private boolean walking = false;
    private int numDeceleration = 0;
    private TextView movementIndicator;

    // Server related
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.sensors = new Sensors(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        final ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.phone1);
        helpButton = (ImageButton) findViewById(R.id.helpButton);
        HelpAlert = (TextView) findViewById(R.id.HelpAlert);

        // Help button implementation
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Directions.this);
                builder.setCancelable(true);
                builder.setIcon(R.drawable.ic_help);
                builder.setTitle("How To Use");
                builder.setMessage("Please hold the phone as shown.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                if (image.getParent() != null) {
                    ((ViewGroup)image.getParent()).removeView(image);
                }
                builder.setView(image);
                builder.create().show();
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference("data");

        movementIndicator = (TextView) findViewById(R.id.movementIndicator);
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

        if (!haveNetwork()) {
            movementIndicator.setText("Please enable WIFI.");
        }
        else {
            switch (sensorType) {
                case Sensor.TYPE_ROTATION_VECTOR:

                    SensorManager.getRotationMatrixFromVector(rMat, event.values);
                    // Extract the rotation vector and convert to a compass bearing
                    compass = Math.round((int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360);

                    // Write to database
                    mDatabase.child("Compass").setValue(compass);

                    // This part run once to initialize the value at the start of the application.
                    if (initialCompass < 0) {
                        initialCompass = compass;
                    }

                    compassDiff = compass - initialCompass;
                    System.out.println(compassDiff);

                    // Down
                    if (walking == true) {
                        movementIndicator.setText("Trolley is moving");
                        mDatabase.child("Walking").setValue(1);
                        if (inRange(compassDiff, 137, 223) || inRange(compassDiff, -223, -137)) {
                            final Animation animation = createAnimation();
                            final ImageButton btnDown = findViewById(R.id.imageDown);
                            btnDown.startAnimation(animation);
                            btnDown.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    view.clearAnimation();
                                }
                            });

                            // Update user's current direction
                            initialCompass += 180;
                            if (initialCompass >= 360) {
                                initialCompass -= 360;
                            }
                        }

                        // Right
                        else if (inRange(compassDiff, 47, 133) || inRange(compassDiff, -313, -227)) {
                            final Animation animation = createAnimation();
                            final ImageButton btnRight = findViewById(R.id.imageRight);
                            btnRight.startAnimation(animation);
                            btnRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    view.clearAnimation();
                                }
                            });

                            initialCompass += 90;
                            if (initialCompass >= 360) {
                                initialCompass -= 360;
                            }
                        }

                        // Left
                        else if (inRange(compassDiff, -133, -47) || inRange(compassDiff, 227, 313)) {
                            final Animation animation = createAnimation();
                            final ImageButton btnLeft = findViewById(R.id.imageLeft);
                            btnLeft.startAnimation(animation);
                            btnLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    view.clearAnimation();
                                }
                            });

                            initialCompass -= 90;
                            if (initialCompass < 0) {
                                initialCompass += 360;
                            }
                        }

                        // Up
                        else if ((compassDiff >= -43) || (compassDiff <= 43)) {
                            final Animation animation = createAnimation();
                            final ImageButton btnUp = findViewById(R.id.imageUp);
                            btnUp.startAnimation(animation);
                            btnUp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    view.clearAnimation();
                                }
                            });
                        }
                    } else {
                        movementIndicator.setText("Trolley is stationary");
                        mDatabase.child("Walking").setValue(0);
                    }
                    break;

                case Sensor.TYPE_LINEAR_ACCELERATION:

                    xValue = event.values[0];
                    yValue = event.values[1];

                    // This part to detect if walking or not
                    // Walking and not turning              walking and turning
                    if (yValue > 0.3 && xValue < 0.3 || (yValue > 0.3 && xValue > 0.3)) {

                        walking = true;
                        numDeceleration = 0;

                    }
                    // Standing and turn                                 standing
                    else if ((yValue < 0.3 && xValue > 0.3) || (yValue < 0.3 && xValue < 0.3)) {
                        numDeceleration++;
                    } else {
                        numDeceleration++;
                    }

                    // Count the number of decelerations
                    if (numDeceleration > 5) {
                        walking = false;

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (walking == false) {
                            numDeceleration = 0;
                        }
                    }

                    break;

                default:
                    break;
            }
        }
    }

    private Animation createAnimation() {
        // Change alpha from fully visible to invisible
        final Animation animation = new AlphaAnimation(1, 0);
        // Duration is set to half a second
        animation.setDuration(167);
        // Do not alter animation rate
        animation.setInterpolator(new LinearInterpolator());
        // Repeat animation infinitely
        animation.setRepeatCount(3);
        // Reverse animation at the end so the button will fade back in
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    // Checks if compass difference is in range of const1 and const2
    private Boolean inRange(float x, float const1, float const2) {
        return x >= const1 && x <= const2;
    }

    // Checks if phone has wifi or mobile data connection
    private boolean haveNetwork()
    {
        boolean isConnected;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
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
}

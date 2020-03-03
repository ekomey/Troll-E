package com.example.tracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

public class Directions extends AppCompatActivity {
    private ImageButton helpButton;
    private TextView HelpAlert;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //int xValue;
        //xValue = Math.round( (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0]) + 360) % 360);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        helpButton = (ImageButton) findViewById(R.id.helpButton);
        HelpAlert = (TextView) findViewById(R.id.HelpAlert);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Directions.this);

                builder.setCancelable(true);
                builder.setTitle("How To Use");
                builder.setMessage("Please make the phone straight");


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HelpAlert.setVisibility(View.VISIBLE);
                    }
                });
                builder.show();
            }
        });

        //if (xValue = 90) {
            final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
         animation.setDuration(500); // duration - half a second
            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            final ImageButton btn = (ImageButton) findViewById(R.id.imageUp);
            btn.startAnimation(animation);
            btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.clearAnimation();

            }
        });
            //}
        //if (xValue = 90) {
            final Animation animation1 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            animation1.setDuration(500); // duration - half a second
            animation1.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation1.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation1.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            final ImageButton btn1 = (ImageButton) findViewById(R.id.imageDown);
            btn1.startAnimation(animation1);
            btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.clearAnimation();

            }
        });
        //}

        //if (xValue = 90){
            final Animation animation2 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            animation2.setDuration(500); // duration - half a second
            animation2.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation2.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation2.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            final ImageButton btn2 = (ImageButton) findViewById(R.id.imageRight);
            btn2.startAnimation(animation2);
            btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.clearAnimation();

            }
        });
        //}
        //if (xValue = 90){
            final Animation animation3 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            animation3.setDuration(500); // duration - half a second
            animation3.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            animation3.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
            animation3.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
            final ImageButton btn3 = (ImageButton) findViewById(R.id.imageLeft);
            btn3.startAnimation(animation3);
            btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.clearAnimation();

            }
        });
        //}

    }
    }
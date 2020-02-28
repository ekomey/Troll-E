package com.example.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

public class Start extends AppCompatActivity {
    private Button btnMove;
    Button flash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        Handler h = new Handler();
//        h.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startFlash();
//            }
//        },3000);

        btnMove = findViewById(R.id.start_btn);

        btnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToDirections();
            }
        });
    }

    public void startFlash()
    {
        flash=(Button)findViewById(R.id.start_btn);

        Animation mAnimation = new AlphaAnimation(1,0);

        mAnimation.setDuration(200);

        mAnimation.setInterpolator(new LinearInterpolator());

        mAnimation.setRepeatCount(5);

        mAnimation.setRepeatMode(Animation.REVERSE);

        flash.startAnimation(mAnimation);
    }

    private void moveToDirections() {
        Intent intent = new Intent(Start.this, Directions.class);
        startActivity(intent);
    }

}
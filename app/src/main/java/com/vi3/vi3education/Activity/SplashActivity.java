package com.vi3.vi3education.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vi3.vi3education.R;
import com.vi3.vi3education.extra.Preferences;


public class SplashActivity extends AppCompatActivity {

    //Imageview
    ImageView img;

    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


        preferences=new Preferences(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);

                if(preferences.get("user_id").isEmpty())
                {
                    Intent intee = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intee);
                }
                else
                {

                    Intent i = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(i);

                }

                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                finish();
            }
        }, 6000);
    }
}
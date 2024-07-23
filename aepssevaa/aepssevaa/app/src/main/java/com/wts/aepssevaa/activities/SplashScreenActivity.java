package com.wts.aepssevaa.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.wts.aepssevaa.R;

public class SplashScreenActivity extends AppCompatActivity {
    Handler handler;
    String user;
    SharedPreferences sharedPreferences;
    Intent intent;

    //
    ImageView imgLogo;
    Animation fromTop,fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        imgLogo=findViewById(R.id.img_logo);
        fromBottom= AnimationUtils.loadAnimation(this,R.anim.frombottom);
        fromTop= AnimationUtils.loadAnimation(this,R.anim.fromtop);

        imgLogo.setAnimation(fromTop);
        ///


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);
        user=sharedPreferences.getString("loginUsername",null);
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (user!=null)
                {
                    intent = new Intent(SplashScreenActivity.this, HomeDashboardActivity.class);

                }
                else
                {
                    intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }


        },2500);
    }
}
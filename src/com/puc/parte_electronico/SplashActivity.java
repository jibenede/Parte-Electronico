package com.puc.parte_electronico;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jose on 6/26/14.
 */
public class SplashActivity extends Activity {
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = MainActivity.getIntent(SplashActivity.this);
                startActivity(intent);
            }
        }, 3000);


    }


}

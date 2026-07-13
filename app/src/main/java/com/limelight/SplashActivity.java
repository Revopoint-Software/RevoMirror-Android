package com.limelight;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.limelight.ui.activity.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        App.getContext().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, PcViewActivity.class));
                finish();
            }
        }, 800);
    }
}

package com.yyt.blue.bluetooth;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import me.wangyuwei.particleview.ParticleView;

public class SplashActivit extends AppCompatActivity implements ParticleView.ParticleAnimListener {
    private ParticleView mParticleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mParticleView = (ParticleView) findViewById(R.id.picview);

        mParticleView.setOnParticleAnimListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mParticleView.startAnim();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onAnimationEnd() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivit.this, MainActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }
}

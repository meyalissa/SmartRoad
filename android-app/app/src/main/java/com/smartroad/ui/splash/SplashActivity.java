package com.smartroad.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.smartroad.databinding.ActivitySplashBinding;
import com.smartroad.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long FADE_DURATION_MS = 600;
    private static final long HOLD_DURATION_MS = 900;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivSplashLogo.setAlpha(0f);
        binding.tvSplashAppName.setAlpha(0f);
        binding.ivSplashLogo.animate().alpha(1f).setDuration(FADE_DURATION_MS).start();
        binding.tvSplashAppName.animate().alpha(1f).setDuration(FADE_DURATION_MS).setStartDelay(150).start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, FADE_DURATION_MS + HOLD_DURATION_MS);
    }
}

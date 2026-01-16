package com.example.ecoswitch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecoswitch.userFiles.MainActivity;

public class loginLoading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_loading);
        // Delay for 3 seconds, then move to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(loginLoading.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close splash screen
        }, 3000);
    }
}
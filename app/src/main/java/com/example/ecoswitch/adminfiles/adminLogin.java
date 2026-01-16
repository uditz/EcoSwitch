package com.example.ecoswitch.adminfiles;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.loginactivity;

public class adminLogin extends AppCompatActivity {
    Button loginAdminButton;
    TextView adminEmail, adminPassword, userRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        loginAdminButton = findViewById(R.id.loginAdminButton);
        adminEmail = findViewById(R.id.adminEmail);
        adminPassword = findViewById(R.id.adminPassword);
        userRedirect = findViewById(R.id.userRedirect);

        loginAdminButton.setOnClickListener(view -> {
            // Handle login button click
            if (!adminEmail.getText().toString().isEmpty() && !adminPassword.getText().toString().isEmpty()) {
                String email = adminEmail.getText().toString();
                String pass = adminPassword.getText().toString();
                // Use equals() to compare string content
                if (email.equals("0") && pass.equals("0")) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent indent = new Intent(adminLogin.this, adminHome.class);
                    startActivity(indent);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        userRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(adminLogin.this, loginactivity.class);
            startActivity(intent);
            finish();
        });
    }
}

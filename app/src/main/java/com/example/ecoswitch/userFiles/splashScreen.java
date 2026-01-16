package com.example.ecoswitch.userFiles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoswitch.R;
import com.example.ecoswitch.loginLoading;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashScreen extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        auth=FirebaseAuth.getInstance();


        try{
            new Handler().postDelayed(() -> {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(this, "User is logged in", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                } else {
                    Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
                    navitgateToLogin();
                }

            },3000);
        }
        catch(Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }






    }
    private void navigateToMainActivity(){
        Intent intent = new Intent(splashScreen.this, loginLoading.class);
        startActivity(intent);
        finish();
    }
    private void navitgateToLogin(){
        Intent intent= new Intent(splashScreen.this, register.class);
        startActivity(intent);
        finish();
    }
}
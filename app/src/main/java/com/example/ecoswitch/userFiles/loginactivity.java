package com.example.ecoswitch.userFiles;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoswitch.R;
import com.example.ecoswitch.adminfiles.adminLogin;
import com.example.ecoswitch.loginLoading;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class loginactivity extends AppCompatActivity {

    private TextView loginRedirect;
    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView loginAdmin;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginactivity);
         loginRedirect = findViewById(R.id.loginToRegisterBtn);
         loginRedirect.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 Intent intent = new Intent(loginactivity.this, register.class);
                 startActivity(intent);
                 finish();
             }
             });
            auth=FirebaseAuth.getInstance();
            loginEmail=findViewById(R.id.adminEmail);
            loginPassword=findViewById(R.id.adminPassword);
            loginButton=findViewById(R.id.loginButton);
            loginAdmin=findViewById(R.id.adminLoginBtn);


            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = loginEmail.getText().toString().trim();
                    String pass = loginPassword.getText().toString().trim();
                    if (!email.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (!pass.isEmpty()) {
                            auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(loginactivity.this,"Login sucessfull",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(loginactivity.this, loginLoading.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(loginactivity.this,"Login failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            loginPassword.setError("Password is required");
                        }
                    }else if(email.isEmpty()){
                        loginEmail.setError("Email is required");
                    }
                    else{
                        loginEmail.setError("Please enter correct email");
                    }

                }
            });

            loginAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(loginactivity.this, adminLogin.class);
                    startActivity(intent);
                    finish();
                }
            });


         }
    }

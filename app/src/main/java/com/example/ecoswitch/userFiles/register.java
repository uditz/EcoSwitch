package com.example.ecoswitch.userFiles;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoswitch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class register extends AppCompatActivity {

    private TextView loginRedirect;
    private FirebaseAuth auth;
    private EditText registerName, registerEmail, registerPassword;
    private Spinner stateSpinner;
    private Button registerButton;
    private String selectedState = ""; // Default empty

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Initialize Views
        loginRedirect = findViewById(R.id.registerToLogin);
        registerName = findViewById(R.id.adminEmail);
        registerEmail = findViewById(R.id.editTextTextPersonName2);
        registerPassword = findViewById(R.id.adminPassword);
        stateSpinner = findViewById(R.id.stateSpinner);
        registerButton = findViewById(R.id.registerbtn);

        // Define Indian States
        String[] indianStates = {
                "Select State", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
                "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
                "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
                "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
                "Uttar Pradesh", "Uttarakhand", "West Bengal", "Andaman and Nicobar Islands",
                "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu", "Lakshadweep", "Delhi",
                "Puducherry", "Ladakh", "Jammu and Kashmir"
        };

        // Set Custom Adapter for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, indianStates) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // Disable "Select State"
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);

        // Handle State Selection
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = (position != 0) ? indianStates[position] : "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedState = "";
            }
        });

        // Redirect to Login Page
        loginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(register.this, loginactivity.class);
            startActivity(intent);
            finish();
        });

        // Register Button Click
        registerButton.setOnClickListener(view -> {
            String name = registerName.getText().toString().trim();
            String email = registerEmail.getText().toString().trim();
            String pass = registerPassword.getText().toString().trim();

            if (name.isEmpty()) {
                registerName.setError("Name is required");
                return;
            }
            if (email.isEmpty()) {
                registerEmail.setError("Email is required");
                return;
            }
            if (pass.isEmpty()) {
                registerPassword.setError("Password is required");
                return;
            }
            if (selectedState.isEmpty()) {
                Toast.makeText(register.this, "Please select a state", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create User in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        // Assign Firebase UID as userId
                        saveUserData(firebaseUser.getUid(), name, email, selectedState);
                    }
                } else {
                    Toast.makeText(register.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Save User Data in Firestore
    private void saveUserData(String userId, String name, String email, String state) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Prepare user data
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("location", state);
        userData.put("userId", userId);  // Now using Firebase UID

        // Save user data in Firestore under users/{userId}
        firestore.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(register.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(register.this, loginactivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(register.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}

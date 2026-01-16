package com.example.ecoswitch.userFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.loginactivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class profileframent extends Fragment {

    private TextView userName, userEmail, userLocation;
    private ImageView profileImage;
    private Button logoutButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_profileframent, container, false);

        // Initialize UI elements
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userLocation = view.findViewById(R.id.userLocation);
        profileImage = view.findViewById(R.id.profileImage);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            fetchUserData(userId);
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Handle logout button click
        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut(); // Sign out the user

            // Navigate to LoginActivity
            Intent intent = new Intent(getActivity(), loginactivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears activity stack
            startActivity(intent);

            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
        });


        return view;
    }

    private void fetchUserData(String userId) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String location = documentSnapshot.getString("location");

                        userName.setText(name);
                        userEmail.setText(email);
                        userLocation.setText(location);
                    } else {
                        Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }
}

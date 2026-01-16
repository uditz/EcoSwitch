package com.example.ecoswitch.userFiles;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecoswitch.R;
import com.example.ecoswitch.databinding.ActivityMainBinding;
import com.example.ecoswitch.userFragments.HomeFragment;
import com.example.ecoswitch.userFragments.historyfragment;
import com.example.ecoswitch.userFragments.processfragment;
import com.example.ecoswitch.userFragments.profileframent;
import com.google.firebase.FirebaseApp;
import com.mapbox.mapboxsdk.Mapbox;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Bitmap capturedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this);

        FirebaseApp.initializeApp(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }


        binding.bottomNavigationView.setBackground(null);


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.process) {
                selectedFragment = new processfragment();
            } else if (item.getItemId() == R.id.history) {
                openHistoryFragment();
                return true;
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new profileframent();
            }
            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

        //  Handle Camera Button Click
        if (binding.camerabtn != null) {
            binding.camerabtn.setOnClickListener(view -> checkCameraPermission());
        }
    }

    // Function to Replace Fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //  Check Camera Permission Before Opening Camera
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    //  Request Camera Permission
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera Permission Denied. Enable it in Settings.", Toast.LENGTH_SHORT).show();
                }
            });

    //  Function to Open Camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    // Handle Camera Result and Pass to HistoryFragment
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    capturedImageBitmap = (Bitmap) result.getData().getExtras().get("data");
                    openHistoryFragment();
                }
            });

    //  Open HistoryFragment and Pass Image
    private void openHistoryFragment() {
        historyfragment hs = new historyfragment();
        if (capturedImageBitmap != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("capturedImage", capturedImageBitmap);
            hs.setArguments(bundle);
        }
        replaceFragment(hs);
    }
}

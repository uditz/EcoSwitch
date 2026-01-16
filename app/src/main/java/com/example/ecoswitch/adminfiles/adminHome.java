package com.example.ecoswitch.adminfiles;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecoswitch.R;
import com.example.ecoswitch.adminFragments.pendingRequestFragment;
import com.example.ecoswitch.adminFragments.requestHistoryFragment;
import com.example.ecoswitch.adminFragments.showUsersFragment;
import com.example.ecoswitch.adminProfileFragment;
import com.example.ecoswitch.databinding.ActivityAdminHomeBinding;
import com.example.ecoswitch.userFragments.processfragment;
import com.google.firebase.FirebaseApp;

public class adminHome extends AppCompatActivity {

    private ActivityAdminHomeBinding binding;

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);

        // Initialize binding
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set content view using binding

        Log.d("AdminHome", "binding: " + binding);
        Log.d("AdminHome", "bottomNavigationView: " + binding.bottomNavigationView);

        if (savedInstanceState == null) {
            replaceFragment(new showUsersFragment());
        }

        // Set listener for Bottom Navigation View
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Use switch with constant IDs

                if(item.getItemId()==R.id.userRecord){
                    selectedFragment=new showUsersFragment();
                }
                else if (item.getItemId() == R.id.adminProcess) {
                    selectedFragment = new pendingRequestFragment();
                }
                else if (item.getItemId() == R.id.allRequest) {
                    selectedFragment = new requestHistoryFragment();
                }
                else if (item.getItemId() == R.id.adminProf) {
                    selectedFragment = new adminProfileFragment();
                }
            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

    }
}

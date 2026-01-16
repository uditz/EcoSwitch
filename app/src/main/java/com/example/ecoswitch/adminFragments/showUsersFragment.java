package com.example.ecoswitch.adminFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoswitch.R;
import com.example.ecoswitch.UserAdapter;
import com.example.ecoswitch.userFiles.User; // Import your existing User class
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class showUsersFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_users, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the list and adapter
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        usersRecyclerView.setAdapter(userAdapter);

        // Fetch data from Firestore
        db.collection("users")
                .get()  // Fetch all the documents from the "users" collection
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documentSnapshots = task.getResult();
                        if (documentSnapshots != null) {
                            // Loop through the documents and add each user to the list
                            for (var document : documentSnapshots) {
                                User user = document.toObject(User.class); // Convert Firestore document to User object
                                userList.add(user); // Add the user to the list
                            }
                            userAdapter.notifyDataSetChanged(); // Notify the adapter to update the RecyclerView
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                    }
                });

        return view;
    }
}

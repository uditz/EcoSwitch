package com.example.ecoswitch.userFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.Request;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class processfragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<Request> requestList;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processfragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        requestList = new ArrayList<>();
        adapter = new RequestAdapter(getContext(), requestList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadUserRequests();

        return view;
    }

    private void loadUserRequests() {
        if (currentUser == null) return;

        db.collection("requests").whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String requestId = doc.getId(); // Get Firestore document ID
                        String userId = doc.getString("userId");
                        String name = doc.getString("name");
                        String location = doc.getString("location");
                        String photoUrl = doc.getString("photoUrl"); // Fetching photoUrl directly
                        String classification = doc.getString("classification");
                        Timestamp timeStamp = doc.getTimestamp("timeStamp");
                        String status = doc.getString("status");


                        // Creating Request object with all fields including requestId
                        Request request = new Request(requestId, userId, name, location, photoUrl,classification, timeStamp, status);
                        requestList.add(request);
                    }
                    adapter.notifyDataSetChanged(); // Update RecyclerView after all data is added
                })
                .addOnFailureListener(e ->
                        System.err.println("Error fetching requests: " + e.getMessage()) // Logs error
                );
    }

}

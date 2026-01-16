package com.example.ecoswitch.adminFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.Request;
import com.example.ecoswitch.userFragments.RequestAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class requestHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;
    private List<Request> requestList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_history, container, false);

        recyclerView = view.findViewById(R.id.requestRecyclerView); // Make sure this ID exists in fragment_request_history.xml
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(getContext(), requestList);
        recyclerView.setAdapter(requestAdapter);

        db = FirebaseFirestore.getInstance();

        // Fetch only 'Approved' or 'Rejected' requests
        db.collection("requests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestList.clear(); // Clear old data

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Request request = doc.toObject(Request.class);
                        String status = request.getStatus();

                        if (status != null && (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Rejected"))) {
                            requestList.add(request);
                        }
                    }

                    if (requestList.isEmpty()) {
                        Toast.makeText(getContext(), "No approved or rejected requests found", Toast.LENGTH_SHORT).show();
                    }

                    requestAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching requests", Toast.LENGTH_SHORT).show());

        return view;
    }
}

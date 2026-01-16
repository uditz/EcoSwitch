package com.example.ecoswitch.adminFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.Request;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class pendingRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private PendingRequestAdapter adapter;
    private List<Request> requestList;
    private FirebaseFirestore db;

    public pendingRequestFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_request, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        requestList = new ArrayList<>();
        adapter = new PendingRequestAdapter(requestList, new PendingRequestAdapter.OnRequestActionListener() {
            @Override
            public void onApprove(Request request) {
                approveRequest(request);
            }

            @Override
            public void onReject(Request request) {
                rejectRequest(request);
            }
        });

        recyclerView.setAdapter(adapter);
        loadPendingRequests(); // Fetch data from Firestore

        return view;
    }

    private void loadPendingRequests() {
        db.collection("requests")
                .whereEqualTo("status", "pending") // Fetch only requests where isApproved is false
                .get()
                .addOnSuccessListener(requestSnapshots -> {
                    requestList.clear();
                    for (DocumentSnapshot requestDoc : requestSnapshots.getDocuments()) {
                        Request request = requestDoc.toObject(Request.class);
                        requestList.add(request);
                    }
                    adapter.notifyDataSetChanged(); // Refresh RecyclerView
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching pending requests", e);
                });
    }


    private void approveRequest(Request request) {
        db.collection("requests").document(request.getRequestId())
                .update("status", "Approved")
                .addOnSuccessListener(aVoid -> {
                    requestList.remove(request);
                    adapter.notifyDataSetChanged();
                });
    }

    private void rejectRequest(Request request) {
        db.collection("requests").document(request.getRequestId())
                .update("status", "Rejected")
                .addOnSuccessListener(aVoid -> {
                    requestList.remove(request);
                    adapter.notifyDataSetChanged();
                });
    }
}

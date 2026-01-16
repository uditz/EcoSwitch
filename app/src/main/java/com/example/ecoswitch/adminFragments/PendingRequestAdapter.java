package com.example.ecoswitch.adminFragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.Request;
import java.util.List;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.RequestViewHolder> {

    private List<Request> requestList;
    private OnRequestActionListener onRequestActionListener;

    public PendingRequestAdapter(List<Request> requestList, OnRequestActionListener onRequestActionListener) {
        this.requestList = requestList;
        this.onRequestActionListener = onRequestActionListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_request_item, parent, false);
        return new RequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);

        // Bind request data
        holder.requesterName.setText(request.getName());
        holder.locationTextView.setText("Location: " + request.getLocation());

        // Load request-specific photo using Glide
        if (request.getPhotoUrl() != null && !request.getPhotoUrl().isEmpty()) {
            Glide.with(holder.requesterImage.getContext())
                    .load(request.getPhotoUrl())
                    .placeholder(R.drawable.myprofilesvg)
                    .error(R.drawable.myprofilesvg)
                    .into(holder.requesterImage);
        } else {
            holder.requesterImage.setImageResource(R.drawable.myprofilesvg); // Default image
        }

        // Set button actions for approval or rejection
        holder.approveButton.setOnClickListener(v -> onRequestActionListener.onApprove(request));
        holder.rejectButton.setOnClickListener(v -> onRequestActionListener.onReject(request));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public interface OnRequestActionListener {
        void onApprove(Request request);
        void onReject(Request request);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView requesterName, locationTextView;
        ImageView requesterImage;
        Button approveButton, rejectButton;

        public RequestViewHolder(View itemView) {
            super(itemView);
            requesterName = itemView.findViewById(R.id.requesterName);
            locationTextView = itemView.findViewById(R.id.Location);
            requesterImage = itemView.findViewById(R.id.requesterImage);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}

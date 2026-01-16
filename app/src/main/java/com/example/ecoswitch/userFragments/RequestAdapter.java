package com.example.ecoswitch.userFragments;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.Request;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private Context context;
    private List<Request> requestList;

    public RequestAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pending_request_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.requesterName.setText(request.getName());
        holder.location.setText(request.getLocation());

        // Handle button status and color
        String status = request.getStatus();
        holder.processButton.setText(status);

        if ("Pending".equals(status)) {
            holder.processButton.setBackgroundColor(ContextCompat.getColor(context, R.color.greenButtonGradientColor));
        } else if ("Approved".equals(status)) {
            holder.processButton.setBackgroundColor(ContextCompat.getColor(context, R.color.blue)); // Change color to blue
        } else if ("Rejected".equals(status)) {
            holder.processButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red)); // Change color to red
        }

        // Format and set the timestamp
        Timestamp timestamp = request.getTimeStamp();
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            String formattedDate = sdf.format(date);
            holder.currentTime.setText(formattedDate);
        } else {
            holder.currentTime.setText("No Timestamp"); // Handle null timestamp
        }

        // Load request-specific photo using Glide
        if (request.getPhotoUrl() != null && !request.getPhotoUrl().isEmpty()) {
            Glide.with(context)
                    .load(request.getPhotoUrl())
                    .placeholder(R.drawable.myprofilesvg) // Placeholder while loading
                    .error(R.drawable.myprofilesvg) // Show if loading fails
                    .into(holder.requesterImage);
        } else {
            holder.requesterImage.setImageResource(R.drawable.myprofilesvg); // Default image
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView requesterName, location, currentTime;
        ImageView requesterImage;
        TextView processButton; // Changed to Button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            requesterName = itemView.findViewById(R.id.requesterName);
            location = itemView.findViewById(R.id.location);
            currentTime = itemView.findViewById(R.id.currentTime);
            requesterImage = itemView.findViewById(R.id.requesterImage);
            processButton = itemView.findViewById(R.id.processButton); // Ensure this is a Button in XML
        }
    }
}

package com.example.ecoswitch;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    // Constructor that takes the user list as a parameter
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    // Inflate the layout for each item and return a ViewHolder
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the individual item layout (user_item.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view); // Return a new ViewHolder
    }

    // Bind the data to the views in the ViewHolder
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position); // Get the user at the given position
        Log.d("UserAdapter", "Binding user: " + user.getName() + ", " + user.getEmail() + ", " + user.getLocation());
        // Check for null values before setting text
        if (user.getName() != null) {
            holder.nameTextView.setText(user.getName());
        } else {
            holder.nameTextView.setText("N/A"); // Or some other default value
        }

        if (user.getEmail() != null) {
            holder.emailTextView.setText(user.getEmail());
        } else {
            holder.emailTextView.setText("N/A"); // Or some other default value
        }

        if (user.getLocation() != null) {
            holder.stateTextView.setText(user.getLocation());
        } else {
            holder.stateTextView.setText("N/A"); // Or some other default value
        }
        if (user.getUserId() != null) {
            holder.userIdTextView.setText(String.valueOf(user.getUserId()));
        } else {
            holder.userIdTextView.setText("N/A"); // Or some other default value
        }

    }

    // Return the total number of items (size of the user list)
    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder class that holds the references to the views in the item layout
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        TextView stateTextView;

        TextView userIdTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userName);
            emailTextView = itemView.findViewById(R.id.userEmail);
            stateTextView = itemView.findViewById(R.id.userLocation);
            userIdTextView= itemView.findViewById(R.id.userId);
        }
    }

}
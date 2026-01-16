package com.example.ecoswitch.userFiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ecoswitch.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] states;

    public CustomSpinnerAdapter(Context context, String[] states) {
        super(context, R.layout.spinner_with_icon, states);
        this.context = context;
        this.states = states;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_with_icon, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.spinner_icon);
        TextView name = convertView.findViewById(R.id.stateName);

        name.setText(states[position]);

        // 🔥 Keep icon always visible (Static)
        icon.setVisibility(View.VISIBLE);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView name = (TextView) convertView;
        name.setText(states[position]);

        return convertView;
    }
}

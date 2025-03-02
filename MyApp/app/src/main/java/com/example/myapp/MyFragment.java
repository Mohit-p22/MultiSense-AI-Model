package com.example.myapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

public class MyFragment extends Fragment {

    private TextView textView;

    // Override onCreateView to define fragment UI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragement_layout, container, false);

        // Initialize UI components here
        textView = view.findViewById(R.id.textView);

        return view;
    }

    // Method to update the UI (for activity communication)
    public void updateText(String message) {
        textView.setText(message);
    }
}


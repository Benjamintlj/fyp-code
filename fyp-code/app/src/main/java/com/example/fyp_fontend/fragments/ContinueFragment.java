package com.example.fyp_fontend.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.utils.ContentManager;

public class ContinueFragment extends Fragment {

    public ContinueFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_continue, container, false);

        view.findViewById(R.id.button).setOnClickListener(v -> ContentManager.nextItem(getContext()));

        return view;
    }
}
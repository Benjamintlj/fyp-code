package com.example.fyp_fontend.activity.gym_management_flow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.network.CognitoNetwork;

public class GymHomeActivity extends AppCompatActivity {

    Button signOutActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_home);

        initViews();
        initListeners();
    }

    private void initViews() {
        signOutActivityButton = findViewById(R.id.signOutActivityButton);
    }

    private void initListeners() {
        signOutActivityButton.setOnClickListener(this::signOut);
    }

    private void signOut(View view) {
        CognitoNetwork.getInstance().signOut(getApplicationContext());
        finish();
    }


}
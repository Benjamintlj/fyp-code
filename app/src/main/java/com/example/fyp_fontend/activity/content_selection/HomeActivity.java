package com.example.fyp_fontend.activity.content_selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.fragments.SubjectFragment;
import com.example.fyp_fontend.network.CognitoNetwork;

public class HomeActivity extends AppCompatActivity {

    Button signOutActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        initListeners();
        initFragments();
    }

    private void initViews() {
        signOutActivityButton = findViewById(R.id.signOutActivityButton);
    }

    private void initListeners() {
        signOutActivityButton.setOnClickListener(this::signOut);
    }

    private void initFragments() {
        SubjectFragment biology = SubjectFragment.newInstance(getString(R.string.biology), R.drawable.biology_bg, "biology");
        SubjectFragment chemistry = SubjectFragment.newInstance(getString(R.string.chemistry), R.drawable.biology_bg, "chemistry");
        SubjectFragment physics = SubjectFragment.newInstance(getString(R.string.physics), R.drawable.biology_bg, "physics");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.biologyFragmentContainerFrameLayout, biology);
        fragmentTransaction.replace(R.id.chemistryFragmentContainerFrameLayout, chemistry);
        fragmentTransaction.replace(R.id.physicsFragmentContainerFrameLayout, physics);

        fragmentTransaction.commit();
    }

    private void signOut(View view) {
        CognitoNetwork.getInstance().signOut(getApplicationContext());
        finish();
    }
}
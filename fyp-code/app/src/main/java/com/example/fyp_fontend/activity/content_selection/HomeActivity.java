package com.example.fyp_fontend.activity.content_selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.MainActivity;
import com.example.fyp_fontend.activity.leaderboard.LeaderboardActivity;
import com.example.fyp_fontend.fragments.SubjectFragment;
import com.example.fyp_fontend.model.Stats.UserStats;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.StatsHandler;
import com.example.fyp_fontend.utils.NavbarHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    TextView streakTextView, gemTextView;
    LinearLayout streakLinearLayout, gemLinearLayout;
    UserStats userStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingScreen();

        getContent();
    }

    private void getContent() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                userStats = StatsHandler.getStats(getApplicationContext());
                handler.post(this::setContent);
            } catch (IOException | JSONException e) {
                Log.e(TAG, "getContent: ", e);
                finish();
            }
        });
    }

    private void loadingScreen() {
        setContentView(R.layout.splash_loading);
        TextView titleTextView = findViewById(R.id.loadingTitleTextView);
        titleTextView.setText(R.string.getting_ready_loading);
    }

    private void setContent() {
        setContentView(R.layout.activity_home);

        initViews();
        initListeners();
        initFragments();
        initNavbar();
    }


    private void initViews() {
        streakTextView = findViewById(R.id.streakTextView);
        gemTextView = findViewById(R.id.gemTextView);
        streakLinearLayout = findViewById(R.id.streakLinearLayout);
        gemLinearLayout = findViewById(R.id.gemLinearLayout);

        streakTextView.setText(String.valueOf(userStats.getStreakStats().getStreak()));
        gemTextView.setText(String.valueOf(userStats.getTotalGems().getNumOfGems()));
    }

    private void initListeners() {
        streakLinearLayout.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), R.string.streak_description, Toast.LENGTH_LONG).show();
        });

        gemLinearLayout.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), R.string.total_gems_description, Toast.LENGTH_LONG).show();
        });
    }

    private void initFragments() {
        SubjectFragment biology = SubjectFragment.newInstance(getString(R.string.biology), R.drawable.biology_bg, LessonSelectionActivity.ARG_BIOLOGY);
        SubjectFragment chemistry = SubjectFragment.newInstance(getString(R.string.chemistry), R.drawable.chemistry_bg, LessonSelectionActivity.ARG_CHEMISTRY);
        SubjectFragment physics = SubjectFragment.newInstance(getString(R.string.physics), R.drawable.physics_bg, LessonSelectionActivity.ARG_PHYSICS);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.biologyFragmentContainerFrameLayout, biology);
        fragmentTransaction.replace(R.id.chemistryFragmentContainerFrameLayout, chemistry);
        fragmentTransaction.replace(R.id.physicsFragmentContainerFrameLayout, physics);

        fragmentTransaction.commit();
    }

    private void initNavbar() {
        BottomNavigationView navbarBottomNavigationView = findViewById(R.id.navbarBottomNavigationView);
        NavbarHandler.initNavbar(navbarBottomNavigationView, this);
    }
}

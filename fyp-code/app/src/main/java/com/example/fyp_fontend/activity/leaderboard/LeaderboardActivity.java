package com.example.fyp_fontend.activity.leaderboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.content_selection.HomeActivity;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.LeaderboardHandler;
import com.example.fyp_fontend.network.S3Handler;
import com.example.fyp_fontend.network.callback.CurrentUserLeaderboardIdCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = "LeaderboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_loading);
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(R.string.loading_leaderboards);

        CognitoNetwork.getInstance().getCurrentUserLeaderboardId(getApplicationContext(), new CurrentUserLeaderboardIdCallback() {
            @Override
            public void onSuccess(String currentLeaderboardId) {

                // if user leaderboard is none
                // create a function to handle that
                if (currentLeaderboardId.equals("none")) {
                    welcomeHandler();
                } else {
                    setContentView(R.layout.activity_leaderboard);
                    initNavbar();
                }

                // else
                // load the leaderboard
            }

            @Override
            public void onFailure(Exception exception) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initNavbar() {
        BottomNavigationView navbarBottomNavigationView = findViewById(R.id.navbarBottomNavigationView);

        navbarBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.subjects) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.leaderboard) {
                // pass
                return true;
            } else {
                return false;
            }
        });

        navbarBottomNavigationView.setSelectedItemId(R.id.leaderboard);
    }

    private void welcomeHandler() {
        setContentView(R.layout.splash_wellcome_leaderboard);

        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(view -> {

            setContentView(R.layout.splash_loading);
            TextView titleTextView = findViewById(R.id.titleTextView);
            titleTextView.setText(R.string.loading_leaderboards);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    LeaderboardHandler.createNewContent(getApplicationContext());
                    handler.post(() -> {
                        Intent intent = new Intent(getApplicationContext(), LeaderboardActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });


        });
    }
}
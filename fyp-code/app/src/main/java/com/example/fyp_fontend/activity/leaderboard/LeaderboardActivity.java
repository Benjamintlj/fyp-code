package com.example.fyp_fontend.activity.leaderboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.fyp_fontend.adapter.LeaderboardAdapter;
import com.example.fyp_fontend.model.LeaderboardModel;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.LeaderboardHandler;
import com.example.fyp_fontend.network.callback.CurrentUserLeaderboardIdCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardActivity extends AppCompatActivity {
    private static final String TAG = "LeaderboardActivity";
    private List<LeaderboardModel> leaderboardModelList;
    private RecyclerView leaderboardRecyclerView;
    private LeaderboardAdapter leaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingScreen();

        CognitoNetwork.getInstance().getCurrentUserLeaderboardId(getApplicationContext(), new CurrentUserLeaderboardIdCallback() {
            @Override
            public void onSuccess(String currentLeaderboardId) {
                if (currentLeaderboardId.equals("none")) {
                    welcomeHandler();
                } else {
                    getContent();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                handlerFailure(getString(R.string.failed_to_load_your_current_leaderboard));
            }
        });
    }

    private void getContent() {
        loadingScreen();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                leaderboardModelList = LeaderboardHandler.getRankings(getApplicationContext());
                handler.post(() -> {
                    setContentView(R.layout.activity_leaderboard);
                    initNavbar();
                    initViews();
                    initRecyclerView();
                });
            } catch (IOException | JSONException e) {
                Log.e(TAG, "getContent: ", e);
                handler.post(() -> handlerFailure(getString(R.string.something_went_wrong)));
            }
        });
    }

    private void initViews() {
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView);
    }

    private void initRecyclerView() {
        leaderboardAdapter = new LeaderboardAdapter(getApplicationContext(), leaderboardModelList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        leaderboardRecyclerView.setLayoutManager(layoutManager);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);
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

            loadingScreen();

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

    private void loadingScreen() {
        setContentView(R.layout.splash_loading);
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(R.string.loading_leaderboards);
    }

    private void handlerFailure(String failureMessage) {
        Toast.makeText(getApplicationContext(), failureMessage, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

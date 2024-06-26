package com.example.fyp_fontend.activity.leaderboard;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.content_selection.HomeActivity;
import com.example.fyp_fontend.adapter.LeaderboardAdapter;
import com.example.fyp_fontend.model.LeaderboardModel;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.example.fyp_fontend.network.LeaderboardHandler;
import com.example.fyp_fontend.network.callback.GetRankingsCallback;
import com.example.fyp_fontend.network.callback.UserAttributeCallback;
import com.example.fyp_fontend.utils.NavbarHandler;
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
    private ImageView badgeImageView;
    private TextView badgeColourTextView;
    private String badge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingScreen();

        startSplashChecks();
    }

    private void startSplashChecks() {
        CognitoNetwork.getInstance().getCurrentUserAttribute(getApplicationContext(), new UserAttributeCallback() {
            @Override
            public void onSuccess(String hasSeenWelcome) {
                if (!Boolean.parseBoolean(hasSeenWelcome)) {
                    welcomeHandler(true);
                } else {
                    CognitoNetwork.getInstance().getCurrentUserAttribute(getApplicationContext(), new UserAttributeCallback() {
                        @Override
                        public void onSuccess(String currentLeaderboardId) {
                            if ("none".equals(currentLeaderboardId)) {
                                welcomeHandler(false);
                            } else {
                                getBadge();
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            handlerFailure(getString(R.string.failed_to_load_your_current_leaderboard));
                        }
                    }, "currentLeaderboardId");
                }
            }

            @Override
            public void onFailure(Exception exception) {
                handlerFailure(getString(R.string.failed_to_load_your_current_leaderboard));
            }
        }, "seenWelcome");
    }

    private void getBadge() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        CognitoNetwork.getInstance().getCurrentUserAttribute(getApplicationContext(), new UserAttributeCallback() {
            @Override
            public void onSuccess(String badge) {
                executor.execute(() -> {
                    try {
                        LeaderboardHandler.getRankings(getApplicationContext(), new GetRankingsCallback() {
                            @Override
                            public void onSuccess(List<LeaderboardModel> rankings) {
                                LeaderboardActivity.this.leaderboardModelList = rankings;
                                handler.post(() -> {
                                    LeaderboardActivity.this.badge = badge;
                                    checkIncreaseInRank();
                                });
                            }

                            @Override
                            public void onFailure() {
                                handler.post(() -> handlerFailure(getString(R.string.something_went_wrong)));
                            }
                        });
                    } catch (IOException e) {
                        handler.post(() -> handlerFailure(getString(R.string.something_went_wrong)));
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                handlerFailure(getString(R.string.failed_to_load_your_current_leaderboard));
            }
        }, "leagueRank");
    }

    private void checkIncreaseInRank() {
        CognitoNetwork.getInstance().getCurrentUserAttribute(getApplicationContext(), new UserAttributeCallback() {
            @Override
            public void onSuccess(String rankChanged) {
                boolean hasRankChanged = Boolean.parseBoolean(rankChanged);
                if (hasRankChanged) {
                    handleNewRank();
                    CognitoNetwork.getInstance().setCurrentUserAttribute(getApplicationContext(), new UserAttributeCallback() {
                        @Override
                        public void onSuccess(String value) {
                            // do nothing
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            handlerFailure(getString(R.string.failed_to_load_your_current_leaderboard));
                        }
                    }, "rankChanged", "false");
                } else {
                    getContent();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                handlerFailure(getString(R.string.failed_to_load_your_current_leaderboard));
            }
        }, "rankChanged");
    }

    private void getContent() {
        setContentView(R.layout.activity_leaderboard);
        initNavbar();
        initViews();
        initRecyclerView();
        setContent();
    }

    private void initViews() {
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView);

        badgeImageView = findViewById(R.id.badgeImageView);
        badgeColourTextView = findViewById(R.id.loadingTitleTextView);
    }

    private void initRecyclerView() {
        leaderboardAdapter = new LeaderboardAdapter(getApplicationContext(), leaderboardModelList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        leaderboardRecyclerView.setLayoutManager(layoutManager);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);
    }

    private void initNavbar() {
        BottomNavigationView navbarBottomNavigationView = findViewById(R.id.navbarBottomNavigationView);
        NavbarHandler.initNavbar(navbarBottomNavigationView, this);
    }

    private void welcomeHandler(boolean isUserNew) {
        setContentView(R.layout.splash_wellcome_leaderboard);

        TextView titleTextView = findViewById(R.id.loadingTitleTextView);
        titleTextView.setText(
                isUserNew ? R.string.competitive_learning : R.string.ready_to_compete
        );

        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(
                isUserNew ? R.string.welcome_to_leaderboards_text : R.string.welcome_back_to_leaderboards
        );

        Button getStartedButton = findViewById(R.id.continueButton);
        getStartedButton.setOnClickListener(view -> {

            loadingScreen();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    LeaderboardHandler.joinLeaderboard(getApplicationContext());
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

    private void handleNewRank() {
        setContentView(R.layout.splash_new_rank);

        badgeImageView = findViewById(R.id.badgeImageView);
        badgeColourTextView = findViewById(R.id.loadingTitleTextView);

        setContent();

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LeaderboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadingScreen() {
        setContentView(R.layout.splash_loading);
        TextView titleTextView = findViewById(R.id.loadingTitleTextView);
        titleTextView.setText(R.string.loading_leaderboards);
    }

    private void setContent() {
        @StringRes int badgeText;
        @DrawableRes int badgeImage;

        switch (badge) {
            case "bronze":
                badgeImage = R.drawable.bronze;
                badgeText = R.string.bronze;
                break;
            case "silver":
                badgeImage = R.drawable.silver;
                badgeText = R.string.silver;
                break;
            case "gold":
                badgeImage = R.drawable.gold;
                badgeText = R.string.gold;
                break;
            default:
                badgeImage = R.drawable.unknown;
                badgeText = R.string.unknown;
                break;
        }

        badgeColourTextView.setText(getString(badgeText));
        badgeImageView.setImageResource(badgeImage);
    }

    private void handlerFailure(String failureMessage) {
        Toast.makeText(getApplicationContext(), failureMessage, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

package com.example.fyp_fontend.activity.stats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.fragments.AchievementFragment;
import com.example.fyp_fontend.model.Stats.UserStats;
import com.example.fyp_fontend.network.StatsHandler;
import com.example.fyp_fontend.network.callback.UserStatsCallback;
import com.example.fyp_fontend.utils.NavbarHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AchievementsActivity extends AppCompatActivity {
    private static final String TAG = "AchievementsActivity";
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
                StatsHandler.getStats(getApplicationContext(), new UserStatsCallback() {
                    @Override
                    public void onSuccess(UserStats userStats) {
                        AchievementsActivity.this.userStats = userStats;
                        handler.post(AchievementsActivity.this::setContent);
                    }

                    @Override
                    public void onFailure() {
                        finish();
                    }
                });
            } catch (IOException | JSONException e) {
                Log.e(TAG, "getContent: ", e);
                finish();
            }
        });
    }

    private void loadingScreen() {
        setContentView(R.layout.splash_loading);
        TextView titleTextView = findViewById(R.id.loadingTitleTextView);
        titleTextView.setText(R.string.loading_lessons);
    }

    private void setContent() {
        setContentView(R.layout.activity_achievements);

        initViews();
    }

    private void initViews() {

        // Streaks
        AchievementFragment achievementFragmentStreak = AchievementFragment.newInstance(
                userStats.getStreakStats().getStreak(),
                userStats.getStreakStats().getBronze(),
                userStats.getStreakStats().getSilver(),
                userStats.getStreakStats().getGold(),
                userStats.getStreakStats().getStreakRank(),
                getString(R.string.streak_description),
                getString(R.string.streak)
        );
        getSupportFragmentManager().beginTransaction().add(R.id.streakFrameLayout, achievementFragmentStreak).commit();

        // Lessons completed
        AchievementFragment achievementFragmentLessonCompleted = AchievementFragment.newInstance(
                userStats.getLessonsCompleted().getNumOfLessonsCompleted(),
                userStats.getLessonsCompleted().getBronze(),
                userStats.getLessonsCompleted().getSilver(),
                userStats.getLessonsCompleted().getGold(),
                userStats.getLessonsCompleted().getLessonsCompletedRank(),
                getString(R.string.lessons_completed_description),
                getString(R.string.lessons_completed_title)
        );
        getSupportFragmentManager().beginTransaction().add(R.id.lessonCompletedFrameLayout, achievementFragmentLessonCompleted).commit();

        // Total gems
        AchievementFragment achievementFragmentGems = AchievementFragment.newInstance(
                userStats.getTotalGems().getNumOfGems(),
                userStats.getTotalGems().getBronze(),
                userStats.getTotalGems().getSilver(),
                userStats.getTotalGems().getGold(),
                userStats.getTotalGems().getGemsRank(),
                getString(R.string.total_gems_description),
                getString(R.string.total_gems_title)
        );
        getSupportFragmentManager().beginTransaction().add(R.id.gemsFrameLayout, achievementFragmentGems).commit();

        // First Places
        AchievementFragment achievementFragmentFirstPlace = AchievementFragment.newInstance(
                userStats.getFirstPlace().getNumOfFirstPlace(),
                userStats.getFirstPlace().getBronze(),
                userStats.getFirstPlace().getSilver(),
                userStats.getFirstPlace().getGold(),
                userStats.getFirstPlace().getFirstPlaceRank(),
                getString(R.string.first_place_description),
                getString(R.string.first_place_title)
        );
        getSupportFragmentManager().beginTransaction().add(R.id.firstPlaceFrameLayout, achievementFragmentFirstPlace).commit();

        // Flawless
        AchievementFragment achievementFragmentFlawless = AchievementFragment.newInstance(
                userStats.getFlawless().getNumOfFlawless(),
                userStats.getFlawless().getBronze(),
                userStats.getFlawless().getSilver(),
                userStats.getFlawless().getGold(),
                userStats.getFlawless().getFlawlessRank(),
                getString(R.string.flawless_description),
                getString(R.string.flawless_title)
        );
        getSupportFragmentManager().beginTransaction().add(R.id.flawlessFrameLayout, achievementFragmentFlawless).commit();

        // Speed run
        AchievementFragment achievementFragmentSpeedRun = AchievementFragment.newInstance(
                userStats.getLessonsCompleted().getNumOfLessonsCompleted(),
                userStats.getLessonsCompleted().getBronze(),
                userStats.getLessonsCompleted().getSilver(),
                userStats.getLessonsCompleted().getGold(),
                userStats.getLessonsCompleted().getLessonsCompletedRank(),
                getString(R.string.speed_run_description),
                getString(R.string.speed_run_title)
        );
        getSupportFragmentManager().beginTransaction().add(R.id.speedFrameLayout, achievementFragmentSpeedRun).commit();

        // Revised
        AchievementFragment achievementFragmentRevised = AchievementFragment.newInstance(
                userStats.getRevisedLessons().getNumOfRevised(),
                userStats.getRevisedLessons().getBronze(),
                userStats.getRevisedLessons().getSilver(),
                userStats.getRevisedLessons().getGold(),
                userStats.getRevisedLessons().getRevisedRank(),
                getString(R.string.revised_description),
                getString(R.string.revision_title)
        );
        getSupportFragmentManager().beginTransaction().add(R.id.revisedFrameLayout, achievementFragmentRevised).commit();

        BottomNavigationView navbarBottomNavigationView = findViewById(R.id.navbarBottomNavigationView);
        NavbarHandler.initNavbar(navbarBottomNavigationView, this);
    }
}
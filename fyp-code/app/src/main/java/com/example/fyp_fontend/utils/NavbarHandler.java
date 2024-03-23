package com.example.fyp_fontend.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.MainActivity;
import com.example.fyp_fontend.activity.content_selection.HomeActivity;
import com.example.fyp_fontend.activity.content_selection.LessonSelectionActivity;
import com.example.fyp_fontend.activity.content_selection.RepetitionActivity;
import com.example.fyp_fontend.activity.leaderboard.LeaderboardActivity;
import com.example.fyp_fontend.activity.stats.AchievementsActivity;
import com.example.fyp_fontend.network.CognitoNetwork;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavbarHandler {
    public static void initNavbar(BottomNavigationView view, Activity activity) {
        if (activity instanceof HomeActivity) {
            view.setSelectedItemId(R.id.subjects);
        } else if (activity instanceof LeaderboardActivity) {
            view.setSelectedItemId(R.id.leaderboard);
        } else if (activity instanceof LessonSelectionActivity) {
            view.setSelectedItemId(R.id.subjects);
        } else if (activity instanceof RepetitionActivity) {
            view.setSelectedItemId(R.id.spacedRepetition);
        } else if (activity instanceof AchievementsActivity) {
            view.setSelectedItemId(R.id.achievement);
        }

        view.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.subjects && !(activity instanceof HomeActivity)) {
                Intent intent = new Intent(activity, HomeActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else if (itemId == R.id.leaderboard && !(activity instanceof LeaderboardActivity)) {
                Intent intent = new Intent(activity, LeaderboardActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else if (itemId == R.id.spacedRepetition && !(activity instanceof RepetitionActivity)) {
                Intent intent = new Intent(activity, RepetitionActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else if (itemId == R.id.achievement && !(activity instanceof AchievementsActivity)) {
                Intent intent = new Intent(activity, AchievementsActivity.class);
                activity.startActivity(intent);
                activity.finish();
            } else if (itemId == R.id.signOut) {
                signOut(activity);
            }
            return true;
        });
    }

    private static void signOut(Activity activity) {
        CognitoNetwork.getInstance().signOut(activity.getApplicationContext());
        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}

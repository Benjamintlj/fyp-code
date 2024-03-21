package com.example.fyp_fontend.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.fyp_fontend.R;
import com.example.fyp_fontend.activity.content_selection.HomeActivity;
import com.example.fyp_fontend.activity.content_selection.LessonSelectionActivity;
import com.example.fyp_fontend.activity.leaderboard.LeaderboardActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavbarHandler {
    public static void initNavbar(BottomNavigationView view, Activity activity) {
        if (activity instanceof HomeActivity) {
            view.setSelectedItemId(R.id.subjects);
        } else if (activity instanceof LeaderboardActivity) {
            view.setSelectedItemId(R.id.leaderboard);
        } else if (activity instanceof LessonSelectionActivity) {
            view.setSelectedItemId(R.id.subjects);
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
            }
            return true;
        });
    }
}

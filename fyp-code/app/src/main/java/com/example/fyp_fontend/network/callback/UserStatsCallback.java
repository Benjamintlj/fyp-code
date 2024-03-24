package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.model.Stats.UserStats;
import com.example.fyp_fontend.model.content_selection.LessonModel;

import java.util.List;

public interface UserStatsCallback {
    void onSuccess(UserStats userStats);
    void onFailure();
}

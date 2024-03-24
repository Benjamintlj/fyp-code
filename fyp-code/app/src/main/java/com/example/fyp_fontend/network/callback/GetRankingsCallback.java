package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.model.LeaderboardModel;
import com.example.fyp_fontend.network.LeaderboardHandler;

import java.util.List;

public interface GetRankingsCallback {
    void onSuccess(List<LeaderboardModel> rankings);
    void onFailure();
}

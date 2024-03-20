package com.example.fyp_fontend.network.callback;

public interface CurrentUserLeaderboardIdCallback {
    void onSuccess(String currentLeaderboardId);

    void onFailure(Exception exception);
}

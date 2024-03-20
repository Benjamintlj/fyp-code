package com.example.fyp_fontend.network;

import android.content.Context;
import android.util.Log;

import com.example.fyp_fontend.activity.leaderboard.LeaderboardActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardHandler {
    private static final String TAG = "LeaderboardHandler";

    public static boolean createNewContent(Context context) throws IOException {
        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));
        String body = com.example.nfc_gym_app.network.HttpHandler.sendHttpRequest("leaderboard/join", "PATCH", json);

        return true;
    }
}

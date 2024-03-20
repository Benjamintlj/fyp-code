package com.example.fyp_fontend.network;

import android.content.Context;
import android.util.Log;

import com.example.fyp_fontend.activity.leaderboard.LeaderboardActivity;
import com.example.fyp_fontend.model.LeaderboardModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardHandler {
    private static final String TAG = "LeaderboardHandler";

    public static void createNewContent(Context context) throws IOException {
        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));
        com.example.nfc_gym_app.network.HttpHandler.sendHttpRequest("leaderboard/join", "PATCH", json);
    }

    public static List<LeaderboardModel> getRankings(Context context) throws IOException, JSONException {
        String username = CognitoNetwork.getInstance().getCurrentUsername(context);
        String url = "leaderboard/rankings?username=" + URLEncoder.encode(username, "UTF-8");
        String body = com.example.nfc_gym_app.network.HttpHandler.sendHttpRequest(url, "GET", null);

        JSONArray jsonArray = new JSONArray(body);
        List<LeaderboardModel> rankings = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            rankings.add(new LeaderboardModel(
                    jsonObject.getString("username"),
                    jsonObject.getInt("score")
            ));
        }

        return rankings;
    }

    public static void updateScore(Context context, int score) throws IOException {
        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));
        json.put("score", score);
        com.example.nfc_gym_app.network.HttpHandler.sendHttpRequest("leaderboard/score", "PATCH", json);
    }
}

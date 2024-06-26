package com.example.fyp_fontend.network;

import android.content.Context;

import com.example.fyp_fontend.model.LeaderboardModel;
import com.example.fyp_fontend.network.callback.GetRankingsCallback;
import com.example.fyp_fontend.network.callback.ResponseCallback;

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

    public static void joinLeaderboard(Context context) throws IOException {
        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));
        HttpHandler.sendHttpRequest("leaderboard/join", "PATCH", context, json, new ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // do nothing
            }

            @Override
            public void onFailure() {
                // do nothing
            }
        });
    }

    public static void getRankings(Context context, GetRankingsCallback getRankingsCallback) throws IOException {
        String username = CognitoNetwork.getInstance().getCurrentUsername(context);
        String url = "leaderboard/rankings?username=" + URLEncoder.encode(username, "UTF-8");
        HttpHandler.sendHttpRequest(url, "GET", context, null, new ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<LeaderboardModel> rankings = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        rankings.add(new LeaderboardModel(
                                jsonObject.getString("username"),
                                jsonObject.getInt("score")
                        ));
                    }

                    getRankingsCallback.onSuccess(rankings);
                } catch (JSONException e) {
                    getRankingsCallback.onFailure();
                }
            }

            @Override
            public void onFailure() {
                getRankingsCallback.onFailure();
            }
        });
    }

    public static void updateScore(Context context, int score) throws IOException {
        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));
        json.put("score", score);
        HttpHandler.sendHttpRequest("leaderboard/score", "PATCH", context, json, new ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // do nothing
            }

            @Override
            public void onFailure() {
                // do nothing
            }
        });
    }
}

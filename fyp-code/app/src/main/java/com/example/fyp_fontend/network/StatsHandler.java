package com.example.fyp_fontend.network;

import android.content.Context;
import android.util.Log;

import com.example.fyp_fontend.model.Stats.UserStats;
import com.example.fyp_fontend.network.callback.ResponseCallback;
import com.example.fyp_fontend.network.callback.UserStatsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class StatsHandler {

    public static void gems(Context context, int gems) throws IOException {
        String url = "stats/total_gems";

        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));
        json.put("gems", gems);

        HttpHandler.sendHttpRequest(url, "PUT", context, json, new ResponseCallback() {
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

    public static void streak(Context context) throws IOException {
        String url = "stats/streak";
        genericPut(context, url);
    }

    public static void lessonComplete(Context context) throws IOException {
        String url = "stats/lessons_completed";
        genericPut(context, url);
    }

    public static void flawless(Context context) throws IOException {
        String url = "stats/flawless";
        genericPut(context, url);
    }

    public static void speedRun(Context context) throws IOException {
        String url = "stats/speed_run";
        genericPut(context, url);
    }

    public static void revisedLessons(Context context) throws IOException {
        String url = "stats/revised_lessons";
        genericPut(context, url);
    }

    private static void genericPut(Context context, String url) throws IOException {
        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));

        HttpHandler.sendHttpRequest(url, "PUT", context, json, new ResponseCallback() {
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

    public static void getStats(Context context, UserStatsCallback userStatsCallback) throws IOException, JSONException {
        String username = CognitoNetwork.getInstance().getCurrentUsername(context);
        String url = "stats?username=" + URLEncoder.encode(username, "UTF-8");

        HttpHandler.sendHttpRequest(url, "GET", context, null, new ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    userStatsCallback.onSuccess(new UserStats(new JSONObject(response)));
                } catch (JSONException e) {
                    Log.e("Get stats", "onSuccess: ", e);
                    userStatsCallback.onFailure();
                }
            }

            @Override
            public void onFailure() {
                Log.e("Get stats", "onFailure: ");
                userStatsCallback.onFailure();
            }
        });
    }
}

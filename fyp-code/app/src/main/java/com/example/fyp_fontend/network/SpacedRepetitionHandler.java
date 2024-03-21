package com.example.fyp_fontend.network;

import android.content.Context;

import com.example.fyp_fontend.model.content_selection.SpacedRepetition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SpacedRepetitionHandler {
    public static SpacedRepetition getSpacedRepetitionData(Context context, String s3Url) throws IOException, JSONException {
        String username = CognitoNetwork.getInstance().getCurrentUsername(context);
        String url = "spaced-repetition?s3_url=" + s3Url + "&username=" + URLEncoder.encode(username, "UTF-8");
        String body = com.example.nfc_gym_app.network.HttpHandler.sendHttpRequest(url, "GET", null);

        JSONObject jsonObject = new JSONObject(body);

        SpacedRepetition spacedRepetition = new SpacedRepetition();
        spacedRepetition.init(
                jsonObject.getLong("last_completed"),
                jsonObject.getLong("time_to_wait")
        );

        return spacedRepetition;
    }

    public static void updateSpacedRepetitionData(Context context, String s3Url, int percentage) throws IOException {
        String url = "spaced-repetition";

        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));
        json.put("s3_url", s3Url);
        json.put("percentage", percentage);

        com.example.nfc_gym_app.network.HttpHandler.sendHttpRequest(url, "PUT", json);
    }
}

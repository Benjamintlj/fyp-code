package com.example.fyp_fontend.network;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.example.fyp_fontend.model.content_selection.LessonModel;
import com.example.fyp_fontend.model.content_selection.SpacedRepetition;
import com.example.fyp_fontend.model.content_selection.SpacedRepetitionEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class SpacedRepetitionHandler {
    private static final String TAG = "SpacedRepetitionHandler";

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

    public static List<LessonModel> getAllSpacedRepetitionLessonsForUser(Context context) throws IOException, JSONException {
        String username = CognitoNetwork.getInstance().getCurrentUsername(context);
        String url = "spaced-repetition?username=" + URLEncoder.encode(username, "UTF-8");
        String body = com.example.nfc_gym_app.network.HttpHandler.sendHttpRequest(url, "GET", null);

        List<LessonModel> lessonModelList = Collections.synchronizedList(new ArrayList<>());
        JSONArray jsonArray = new JSONArray(body);

        final CountDownLatch countDownLatch = new CountDownLatch(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            new Thread(() -> {
                try {
                    SpacedRepetition spacedRepetition = new SpacedRepetition();
                    spacedRepetition.init(
                            jsonObject.getLong("last_completed"),
                            jsonObject.getLong("time_to_wait")
                    );

                    if (!spacedRepetition.getSpacedRepetitionEnum().equals(SpacedRepetitionEnum.GREEN)) {
                        String s3Url = jsonObject.getString("s3_url");
                        String lessonName = S3Handler.getInstance(context).getNameFromMetadata(s3Url);
                        if (lessonName != null) {
                            LessonModel lessonModel = new LessonModel(lessonName, s3Url);
                            lessonModel.setSpacedRepetition(spacedRepetition);
                            lessonModelList.add(lessonModel);
                        }
                    }
                } catch (JSONException | AmazonS3Exception e) {
                    Log.e(TAG, "getAllSpacedRepetitionLessonsForUser: ", e);;
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "getAllSpacedRepetitionLessonsForUser: ", e);
        }

        return lessonModelList;
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

package com.example.fyp_fontend.network;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.example.fyp_fontend.model.content_selection.LessonModel;
import com.example.fyp_fontend.model.content_selection.SpacedRepetition;
import com.example.fyp_fontend.model.content_selection.SpacedRepetitionEnum;
import com.example.fyp_fontend.network.callback.LessonModelsCallback;
import com.example.fyp_fontend.network.callback.ResponseCallback;
import com.example.fyp_fontend.network.callback.SpacedRepetitionDataCallback;

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

    public static void getSpacedRepetitionData(Context context, String s3Url, SpacedRepetitionDataCallback spacedRepetitionDataCallback) throws IOException, JSONException {
        String username = CognitoNetwork.getInstance().getCurrentUsername(context);
        String url = "spaced-repetition?s3_url=" + s3Url + "&username=" + URLEncoder.encode(username, "UTF-8");
        HttpHandler.sendHttpRequest(url, "GET", context, null, new ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    SpacedRepetition spacedRepetition = new SpacedRepetition();
                    spacedRepetition.init(
                            context,
                            jsonObject.getLong("last_completed"),
                            jsonObject.getLong("time_to_wait")
                    );

                    spacedRepetitionDataCallback.onSuccess(spacedRepetition);
                } catch (JSONException e) {
                    spacedRepetitionDataCallback.onFailure();
                }
            }

            @Override
            public void onFailure() {
                spacedRepetitionDataCallback.onFailure();
            }
        });
    }

    public static void getAllSpacedRepetitionLessonsForUser(Context context, LessonModelsCallback lessonModelsCallback) throws IOException, JSONException {
        String username = CognitoNetwork.getInstance().getCurrentUsername(context);
        String url = "spaced-repetition?username=" + URLEncoder.encode(username, "UTF-8");
        HttpHandler.sendHttpRequest(url, "GET", context, null, new ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    List<LessonModel> lessonModelList = Collections.synchronizedList(new ArrayList<>());
                    JSONArray jsonArray = new JSONArray(response);

                    final CountDownLatch countDownLatch = new CountDownLatch(jsonArray.length());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        new Thread(() -> {
                            try {
                                SpacedRepetition spacedRepetition = new SpacedRepetition();
                                spacedRepetition.init(
                                        context,
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

                    lessonModelsCallback.onSuccess(lessonModelList);
                } catch (JSONException e) {
                    lessonModelsCallback.onFailure();
                }
            }

            @Override
            public void onFailure() {
                lessonModelsCallback.onFailure();
            }
        });
    }

    public static void updateSpacedRepetitionData(Context context, String s3Url, int percentage) throws IOException {
        String url = "spaced-repetition";

        Map<String, Object> json = new HashMap<>();
        json.put("username", CognitoNetwork.getInstance().getCurrentUsername(context));
        json.put("s3_url", s3Url);
        json.put("percentage", percentage);

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
}

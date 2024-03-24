package com.example.fyp_fontend.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fyp_fontend.network.callback.AuthTokenCallback;
import com.example.fyp_fontend.network.callback.ResponseCallback;
import com.example.fyp_fontend.utils.Globals;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HttpHandler {
    private static final String TAG = "HttpHandler";

    private static final Gson gson = new Gson();

    public static void sendHttpRequest(String path, String method, Context context, Map<String, Object> body, final ResponseCallback callback) {
        CognitoNetwork.getInstance().getAccessToken(context, new AuthTokenCallback() {
            @Override
            public void onSuccess(String authToken) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    HttpURLConnection httpURLConnection = null;
                    try {
                        httpURLConnection = getHttpURLConnection(path, method, authToken);
                        writeJson(method, body, httpURLConnection);
                        String response = getResponse(httpURLConnection);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(response));
                    } catch (IOException e) {
                        Log.e(TAG, "onSuccess: ", e);
                        new Handler(Looper.getMainLooper()).post(callback::onFailure);
                    } finally {
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }
                });
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "onFailure: ");
                new Handler(Looper.getMainLooper()).post(callback::onFailure);
            }
        });
    }


    @NonNull
    private static HttpURLConnection getHttpURLConnection(String path, String method, String authToken) throws IOException {
        HttpURLConnection httpURLConnection;
        URL url = new URL(Globals.ecsUrl + path);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + authToken);
        return httpURLConnection;
    }

    @NotNull
    private static String getResponse(HttpURLConnection httpURLConnection) throws IOException {
        int statusCode = httpURLConnection.getResponseCode();
        Log.d("test", "getResponse: " + String.valueOf(statusCode));
        if (statusCode >= 200 && statusCode < 300) {
            InputStream inputStream = httpURLConnection.getInputStream();
            return readStream(inputStream);
        } else {
            throw new IOException(String.valueOf(statusCode));
        }
    }

    private static String readStream(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder response = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }

    private static void writeJson(String method, Map<String, Object> body, HttpURLConnection httpURLConnection) throws IOException {
        if (method.equals("POST") || method.equals("PATCH") || method.equals("PUT")) {
            httpURLConnection.setDoInput(true);
            String json = gson.toJson(body);
            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(json);
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        }
    }
}
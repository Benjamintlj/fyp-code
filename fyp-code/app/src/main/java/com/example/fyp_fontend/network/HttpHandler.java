package com.example.nfc_gym_app.network;

import android.util.Log;

import androidx.annotation.NonNull;

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

public class HttpHandler {
    private static final String TAG = "HttpHandler";

    private static final Gson gson = new Gson();

    public static String sendHttpRequest(String path, String method, Map<String, Object> body) throws IOException {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = getHttpURLConnection(path, method);
            writeJson(method, body, httpURLConnection);
            return getResponse(httpURLConnection);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String path, String method) throws IOException {
        HttpURLConnection httpURLConnection;
        URL url = new URL(Globals.ecsUrl + path);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
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
        if (method.equals("POST") || method.equals("PATCH")) {
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
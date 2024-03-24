package com.example.fyp_fontend.network.callback;

public interface AuthTokenCallback {
    void onSuccess(String authToken);
    void onFailure();
}

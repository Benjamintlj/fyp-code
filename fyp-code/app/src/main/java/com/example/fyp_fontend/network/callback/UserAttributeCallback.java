package com.example.fyp_fontend.network.callback;

public interface UserAttributeCallback {
    void onSuccess(String value);

    void onFailure(Exception exception);
}

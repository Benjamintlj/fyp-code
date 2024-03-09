package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.network.CognitoNetwork;

public interface SessionValidationCallback {
    void onSessionValidationValid();
    void onSessionValidationInvalid();
    void onSessionValidationFailure(CognitoNetwork.Responses response);
}

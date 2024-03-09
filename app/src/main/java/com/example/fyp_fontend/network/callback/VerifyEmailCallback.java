package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.network.CognitoNetwork;

public interface VerifyEmailCallback {
    void onVerifySuccess();
    void onVerifyFailure(CognitoNetwork.Responses response);
}

package com.example.fyp_fontend.network.callback;

import com.example.fyp_fontend.network.CognitoNetwork;

public interface ResetPasswordCallback {
    void onResetPasswordSuccess();
    void onResetPasswordFailure(CognitoNetwork.Responses responses);
}
